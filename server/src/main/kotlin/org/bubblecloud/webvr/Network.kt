package org.bubblecloud.webvr

import logger
import org.bubblecloud.webvr.model.Envelope
import org.bubblecloud.webvr.model.Message
import org.bubblecloud.webvr.model.Node
import org.bubblecloud.webvr.model.Session
import org.bubblecloud.webvr.util.Mapper
import org.glassfish.grizzly.websockets.Broadcaster
import org.glassfish.grizzly.websockets.OptimizedBroadcaster
import org.glassfish.grizzly.websockets.WebSocket
import java.util.*
import java.util.logging.Level

class Network() {

    private val log = logger()

    private var broadcaster: Broadcaster = OptimizedBroadcaster()

    private val mapper = Mapper()

    private val sessions: MutableMap<WebSocket, Session> = HashMap()

    @Synchronized fun addSession(session: Session) {
        log.info("Session connected: " + session.remoteHost + ":" + session.remotePort)
        sessions[session.socket] = session
    }

    @Synchronized fun removeSession(socket: WebSocket) {
        val session: Session? = sessions[socket]
        if (session != null) {
            log.info("Session disconnected: ${session.remoteHost}:${session.remotePort}")
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
                if (value is Message) {
                    if (value.type.equals("handshake-request")) {
                        log.info("Handshake accepted : ${session.remoteHost}:${session.remotePort} (${value.properties["software"]})")

                        val responseEnvelope = Envelope()
                        val handshakeResponse = Message("handshake-response", mapOf(
                                "software" to "kotlin-web-vr",
                                "protocol-dialect" to "vr-state-synchronisation",
                                "protocol-version" to "1.0",
                                "accepted" to "true"
                        ))
                        val values : MutableList<Any> = mutableListOf()
                        values.add(handshakeResponse)
                        values.addAll(CELL.getNodes())
                        mapper.writeValuesToEnvelope(responseEnvelope, values)
                        socket.send(mapper.writeValue(responseEnvelope))
                    }
                }
                if (value is Node) {
                    if (!value.removed) {
                        if (CELL.hasNode(value.uri)) {
                            CELL.updateNode(value)
                        } else {
                            CELL.addNode(value)
                        }
                    } else {
                        CELL.removeNode(value.uri)
                    }
                    nodes.add(value)
                }
            }

            if (nodes.size > 0) {
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