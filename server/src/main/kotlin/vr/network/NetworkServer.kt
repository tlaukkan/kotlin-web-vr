package vr.network

import logger
import vr.model.*
import vr.network.model.*
import vr.util.Mapper
import org.glassfish.grizzly.websockets.Broadcaster
import org.glassfish.grizzly.websockets.OptimizedBroadcaster
import org.glassfish.grizzly.websockets.WebSocket
import vr.Cell
import java.util.*
import java.util.logging.Level

class NetworkServer() {

    private val log = logger()

    private var broadcaster: Broadcaster = OptimizedBroadcaster()

    private val mapper = Mapper()

    private val cells: MutableMap<String, Cell> = TreeMap()

    private val sessions: MutableMap<WebSocket, Session> = HashMap()

    @Synchronized fun addCell(cell: Cell) {
        cells.put(cell.cellUri, cell)
    }

    @Synchronized fun getCells() : Collection<Cell> {
        return cells.values
    }

    @Synchronized fun getCellNames() : Array<String> {
        return cells.keys.toTypedArray()
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

            val cellNodeUpdates : MutableMap<String, MutableList<Any>> = mutableMapOf()

            for (value in values) {
                if (value is HandshakeRequest) {
                    log.info("Handshake accepted : ${session.remoteHost}:${session.remotePort} (${value.software})")

                    val responseEnvelope = Envelope()
                    val handshakeResponse = HandshakeResponse(
                            "kotlin-web-vr",
                            "vr-state-synchronisation",
                            "1.0",
                            getCellNames(),
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
                        val responseEnvelope = Envelope()
                        val cellSelectResponse = LinkResponse(true, value.clientCellUris, value.serverCellUris, "")
                        val values: MutableList<Any> = mutableListOf()
                        values.add(cellSelectResponse)
                        for (cellUri in session.serverCellUris) {
                            var cell = cells[cellUri]
                            if (cell != null) {
                                values.addAll(cell.getNodes())
                            }
                        }
                        mapper.writeValuesToEnvelope(responseEnvelope, values)
                        socket.send(mapper.writeValue(responseEnvelope))
                        log.info("Linked : ${session.remoteHost}:${session.remotePort} server cells ${value.serverCellUris}, client cells: ${value.clientCellUris}")
                    } else {
                        val responseEnvelope = Envelope()
                        val cellSelectResponse = LinkResponse(true, arrayOf(), arrayOf(), "No such cell(s): $notFoundServerCellUris")
                        val values: MutableList<Any> = mutableListOf()
                        values.add(cellSelectResponse)
                        mapper.writeValuesToEnvelope(responseEnvelope, values)
                        socket.send(mapper.writeValue(responseEnvelope))
                        log.info("Link failed : ${session.remoteHost}:${session.remotePort} server cells ${value.serverCellUris}, client cells: ${value.clientCellUris}")
                    }
                }

                if (value is Node) {
                    val cellUri: String
                    if (value.url.contains('/')) {
                        cellUri = value.url.substring(0, value.url.lastIndexOf('/'))
                    } else {
                        cellUri = ""
                    }
                    val cell = cells[cellUri]
                    if (cell != null) {
                        if (!value.removed) {
                            if (cell.hasNode(value.url)) {
                                cell.updateNode(value)
                            } else {
                                cell.addNode(value)
                            }
                        } else {
                            cell.removeNode(value.url)
                        }

                        if (!cellNodeUpdates.containsKey(cellUri)) {
                            cellNodeUpdates[cellUri] = mutableListOf()
                        }

                        cellNodeUpdates[cellUri]!!.add(value)
                        log.info("Applied received node modification ${value.url} for cell $cellUri")
                    } else {
                        log.warning("Failed to apply received node ${value.url} modification. No such cell: $cellUri")
                    }
                }
            }

            for (nodes in cellNodeUpdates.values) {
                val broadcastEnvelope = Envelope()
                mapper.writeValuesToEnvelope(broadcastEnvelope, nodes)
                //TODO limit broadcasting to clients linked to cells in question
                broadcast(broadcastEnvelope)
            }

        } catch (ex: Exception) {
            log.log(Level.SEVERE, "Failed to process incoming message.", ex)
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