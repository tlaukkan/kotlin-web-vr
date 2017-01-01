package vr.network

import logger
import vr.model.*
import vr.network.model.*
import vr.util.Mapper
import org.glassfish.grizzly.websockets.Broadcaster
import org.glassfish.grizzly.websockets.OptimizedBroadcaster
import org.glassfish.grizzly.websockets.WebSocket
import vr.Cell
import vr.VrServer
import java.util.*
import java.util.logging.Level

class NetworkServer(val server: VrServer) {

    private val log = logger()

    private var broadcaster: Broadcaster = OptimizedBroadcaster()

    private val mapper = Mapper()

    private val cells: MutableMap<String, Cell> = TreeMap()

    private val sessions: MutableMap<WebSocket, Session> = HashMap()

    @Synchronized fun addCell(cell: Cell) {
        if (cell.remote) {
            log.info("Added remote cell ${cell.url}")
        } else {
            log.info("Added local cell ${cell.url}")
        }
        cells.put(cell.url, cell)
    }

    @Synchronized fun getCells() : Array<Cell> {
        return cells.values.toTypedArray()
    }

    @Synchronized fun getCellUris() : Array<String> {
        return cells.keys.toTypedArray()
    }

    @Synchronized fun hasCell(cellUri: String) : Boolean {
        return cells.containsKey(cellUri)
    }


    @Synchronized fun getCell(cellUri: String) : Cell {
        if (cells.containsKey(cellUri)) {
            return cells[cellUri]!!
        } else {
            throw IllegalArgumentException("No such cell: $cellUri")
        }
    }

    @Synchronized fun addSession(session: Session) {
        log.info("Session connected : " + session.remoteHost + ":" + session.remotePort)
        sessions[session.socket] = session
    }

    @Synchronized fun removeSession(socket: WebSocket) {
        val session: Session? = sessions[socket]
        if (session != null) {
            log.info("Session disconnected : ${session.remoteHost}:${session.remotePort}")
            sessions.remove(socket)
        }
    }

    @Synchronized fun receive(socket: WebSocket, jsonString: String) {
        try {
            val session = sessions[socket] ?: return
            val envelope = mapper.readValue(jsonString, Envelope::class.java)

            val values = mapper.readValuesFromEnvelope(envelope)

            val receivedNodes : MutableList<Node> = mutableListOf()
            for (value in values) {
                if (value is HandshakeRequest) {
                    session.remoteServerUrl = value.clientServerUrl
                    if (session.remoteServerUrl == null) {
                        log.info("Client handshake accepted : ${session.remoteHost}:${session.remotePort} (${value.software}) send server cell URIs: ${cells.keys} ")
                    } else {
                        log.info("Server handshake accepted : ${session.remoteHost}:${session.remotePort} (${session.remoteServerUrl}, ${value.software}) send server cell URIs: ${cells.keys} ")
                    }

                    val responseEnvelope = Envelope()
                    val handshakeResponse = HandshakeResponse(
                            "kotlin-web-vr",
                            "vr-state-synchronisation",
                            "1.0",
                            getCellUris(),
                            true)
                    val values : MutableList<Any> = mutableListOf()
                    values.add(handshakeResponse)
                    mapper.writeValuesToEnvelope(responseEnvelope, values)
                    socket.send(mapper.writeValue(responseEnvelope))
                }

                if (value is LinkRequest) {
                    var serverCellsFound = true
                    var notFoundServerCellUris: MutableList<String> = mutableListOf()
                    for (cellUri in value.serverCellUris) {
                        if (!cells.containsKey(cellUri)) {
                            serverCellsFound = false
                            notFoundServerCellUris.add(cellUri)
                        }
                    }
                    if (serverCellsFound) {
                        session.serverCellUris = value.serverCellUris.toList()
                        session.clientCellUris = value.clientCellUris.toList()

                        val expandedServerCellUris: MutableList<String> = mutableListOf()
                        expandedServerCellUris.addAll(value.serverCellUris)

                        val expandedClientCellUris: MutableList<String> = mutableListOf()
                        expandedClientCellUris.addAll(value.clientCellUris)

                        val neighbours: MutableList<Neighbour> = mutableListOf()
                        for (serverCellUri in value.serverCellUris) {
                            var serverCell = cells[serverCellUri]!!
                            for (neighbourCellUri in serverCell.neighbours.keys) {
                                val neighbourCell = cells[neighbourCellUri]!!
                                if (session.remoteServerUrl == null || !neighbourCell.url.startsWith(session.remoteServerUrl!!)) {
                                    expandedServerCellUris.add(neighbourCell.url)
                                }
                                neighbours.add(Neighbour(serverCell.url, neighbourCell.url, serverCell.neighbours[neighbourCell.url]!!))
                            }
                        }

                        val responseEnvelope = Envelope()
                        val cellSelectResponse = LinkResponse(true, value.clientCellUris, expandedServerCellUris.toTypedArray(),neighbours.toTypedArray())
                        val values: MutableList<Any> = mutableListOf()
                        values.add(cellSelectResponse)

                        val handledCellUris: MutableSet<String> = mutableSetOf()
                        for (cellUri in session.serverCellUris) {
                            var cell = cells[cellUri]!!
                            if (!handledCellUris.contains(cell.url)) {
                                values.addAll(cell.getNodes())
                                handledCellUris.add(cell.url)
                                for (neighbourCellUri in cell.neighbours.keys) {
                                    val neighbourCell = cells[neighbourCellUri]!!
                                    if (session.remoteServerUrl == null || !neighbourCell.url.startsWith(session.remoteServerUrl!!)) {
                                        if (!handledCellUris.contains(neighbourCell.url)) {
                                            values.addAll(neighbourCell.getNodes())
                                            handledCellUris.add(neighbourCell.url)
                                        }
                                    }
                                }
                            }
                        }

                        for (clientCellUri in value.clientCellUris) {
                            if (!cells.containsKey(clientCellUri)) {
                                cells[clientCellUri] = Cell(clientCellUri, true)
                            }
                        }

                        for (neighbour in value.neighbours) {
                            val cellOne = cells[neighbour.cellUriOne]
                            val cellTwo = cells[neighbour.cellUriTwo]
                            val neighbourVector = neighbour.oneTwoDeltaVector

                            if (!cellOne!!.neighbours.containsKey(cellTwo!!.url)) {
                                cellOne!!.neighbours[cellTwo!!.url] = neighbourVector
                                cellTwo!!.neighbours[cellOne!!.url] = DataVector3(
                                        -neighbourVector.x,
                                        -neighbourVector.y,
                                        -neighbourVector.z
                                )
                                log.info("Added neighbours: ${cellOne.url} ${cellOne.neighbours[neighbour.cellUriTwo]} - ${cellTwo.url} ${cellTwo.neighbours[neighbour.cellUriOne]}")
                            }
                        }

                        mapper.writeValuesToEnvelope(responseEnvelope, values)
                        socket.send(mapper.writeValue(responseEnvelope))
                        log.info("Link accepted : ${session.remoteHost}:${session.remotePort} server cells ${value.serverCellUris.toList()}, client cells: ${value.clientCellUris.toList()}")
                    } else {
                        val responseEnvelope = Envelope()
                        val cellSelectResponse = LinkResponse(true, arrayOf(), arrayOf(), arrayOf(), "No such cell(s): $notFoundServerCellUris")
                        val values: MutableList<Any> = mutableListOf()
                        values.add(cellSelectResponse)
                        mapper.writeValuesToEnvelope(responseEnvelope, values)
                        socket.send(mapper.writeValue(responseEnvelope))
                        log.info("Link failed : ${session.remoteHost}:${session.remotePort} server cells ${value.serverCellUris.toList()}, client cells: ${value.clientCellUris.toList()}")
                    }
                }

                if (value is Node) {
                    receivedNodes.add(value)
                }
            }

            processReceivedNodes(receivedNodes)

        } catch (ex: Exception) {
            log.log(Level.SEVERE, "Failed to process incoming message.", ex)
        }
    }

    fun processReceivedNodes(receivedNodes: MutableList<Node>) {
        val cellNodeUpdates: MutableMap<String, MutableList<Any>> = mutableMapOf()
        for (node in receivedNodes) {
            val cellUri: String
            var nodeId: String
            if (node.url.contains('/')) {
                cellUri = node.url.substring(0, node.url.lastIndexOf('/'))
                nodeId = node.url.substring(node.url.lastIndexOf('/') + 1)
                if (nodeId.equals("00000000-0000-0000-0000-000000000000")) {
                    nodeId = UUID.randomUUID().toString()
                    node.url = cellUri + "/" + nodeId
                    log.info("Generated URL for new node of type ${node.javaClass.simpleName}: ${node.url}")
                }
            } else {
                return
            }
            val cell = cells[cellUri]
            if (cell != null) {
                if (!node.removed) {
                    if (cell.hasNode(node.url)) {
                        cell.updateNode(node)
                    } else {
                        cell.addNode(node)
                    }
                } else {
                    cell.removeNode(node.url)
                }

                if (!cellNodeUpdates.containsKey(cellUri)) {
                    cellNodeUpdates[cellUri] = mutableListOf()
                }

                cellNodeUpdates[cellUri]!!.add(node)
                //log.info("Applied received node modification ${node.url} for cell $cellUri")
            } else {
                log.warning("Failed to apply received node ${node.url} modification. No such cell: $cellUri")
            }
        }

        for (nodes in cellNodeUpdates.values) {
            for (value in nodes) {

            }
            val broadcastEnvelope = Envelope()
            mapper.writeValuesToEnvelope(broadcastEnvelope, nodes)
            //TODO limit broadcasting to clients linked to cells in question
            broadcast(broadcastEnvelope)
        }
    }

    @Synchronized fun broadcast(envelope: Envelope) {
        val jsonString = mapper.writeValue(envelope)
        broadcaster.broadcast(sessions.keys, jsonString)
    }

    fun  getSession(socket: WebSocket?): Session? {
        return sessions[socket]
    }


}