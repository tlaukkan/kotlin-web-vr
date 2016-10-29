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
        cells.put(cell.name, cell)
    }

    @Synchronized fun getCells() : Collection<Cell> {
        return cells.values
    }

    @Synchronized fun getCellNames() : Array<String> {
        return cells.keys.toTypedArray()
    }

    @Synchronized fun getCell(name: String) : Cell {
        if (cells.containsKey(name)) {
            return cells[name]!!
        } else {
            throw IllegalArgumentException("No such cell: $name")
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

            val nodes : MutableList<Any> = mutableListOf()

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

                if (value is CellSelectRequest) {
                    if (cells.containsKey(value.cellName)) {
                        session.cell = cells[value.cellName]!!
                        val responseEnvelope = Envelope()
                        val cellSelectResponse = CellSelectResponse(true, value.cellName, "")
                        val values : MutableList<Any> = mutableListOf()
                        values.add(cellSelectResponse)
                        values.addAll(session.cell!!.getNodes())
                        mapper.writeValuesToEnvelope(responseEnvelope, values)
                        socket.send(mapper.writeValue(responseEnvelope))
                        log.info("Cell selected : ${session.remoteHost}:${session.remotePort} ${value.cellName}")
                    } else {
                        val responseEnvelope = Envelope()
                        val cellSelectResponse = CellSelectResponse(true, "", "No such cell: ${value.cellName}")
                        val values : MutableList<Any> = mutableListOf()
                        values.add(cellSelectResponse)
                        mapper.writeValuesToEnvelope(responseEnvelope, values)
                        socket.send(mapper.writeValue(responseEnvelope))
                    }
                }

                if (value is Node) {
                    if (session.cell != null) {
                        if (!value.removed) {
                            if (session.cell!!.hasNode(value.url)) {
                                session.cell!!.updateNode(value)
                            } else {
                                session.cell!!.addNode(value)
                            }
                        } else {
                            session.cell!!.removeNode(value.url)
                        }
                        nodes.add(value)
                    }
                }
            }

            if (nodes.size > 0 && session.cell != null) {
                val broadcastEnvelope = Envelope()
                mapper.writeValuesToEnvelope(broadcastEnvelope, nodes)
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