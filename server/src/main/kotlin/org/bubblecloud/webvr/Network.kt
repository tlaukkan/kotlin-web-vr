package org.bubblecloud.webvr

import com.fasterxml.jackson.databind.ObjectMapper
import logger
import org.bubblecloud.webvr.model.Envelope
import org.bubblecloud.webvr.model.Message
import org.bubblecloud.webvr.model.Session
import org.glassfish.grizzly.websockets.Broadcaster
import org.glassfish.grizzly.websockets.OptimizedBroadcaster
import org.glassfish.grizzly.websockets.WebSocket
import java.util.*
import java.util.logging.Level

class Network() {

    private val log = logger()

    private var broadcaster: Broadcaster = OptimizedBroadcaster()

    private val mapper: ObjectMapper = ObjectMapper()

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

            val messages = envelope.messages
            if (messages != null && messages.size == 1 && messages[0].type.equals("handshake-request")) {
                log.info("Handshake accepted : ${session.remoteHost}:${session.remotePort} (${messages[0].properties["software"]})")

                val responseEnvelope = Envelope()
                val handshakeResponse = Message("handshake-response", mapOf(
                        "software" to "kotlin-web-vr",
                        "protocol-dialect" to "vr-state-synchronisation",
                        "protocol-version" to "1.0",
                        "accepted" to "true"
                ))

                responseEnvelope.nodes = CELL.getNodes()
                responseEnvelope.messages = listOf(handshakeResponse)
                socket.send(mapper.writeValueAsString(responseEnvelope))
            }

            val nodes = envelope.nodes
            if (nodes != null) {
                broadcast(Envelope(null, nodes))

                for (node in nodes) {
                    if (!node.removed) {
                        if (CELL.hasNode(node.uri)) {
                            CELL.updateNode(node)
                        } else {
                            CELL.addNode(node)
                        }
                    } else {
                        CELL.removeNode(node.uri)
                    }
                }
            }
        } catch (ex: Exception) {
            log.log(Level.SEVERE, "Failed to process incoming message.", ex)
        }
    }

    @Synchronized fun broadcast(envelope: Envelope) {
        val jsonString = mapper.writeValueAsString(envelope)
        broadcaster.broadcast(sessions.keys, jsonString)
    }

    fun  getSession(socket: WebSocket?): Session? {
        return sessions[socket]
    }


}