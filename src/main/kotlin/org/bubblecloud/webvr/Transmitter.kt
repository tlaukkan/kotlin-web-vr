package org.bubblecloud.webvr

import com.fasterxml.jackson.databind.ObjectMapper
import logger
import org.bubblecloud.webvr.model.Envelope
import org.glassfish.grizzly.websockets.Broadcaster
import org.glassfish.grizzly.websockets.OptimizedBroadcaster
import org.glassfish.grizzly.websockets.WebSocket
import java.util.*

class Transmitter() {

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
            log.info("Session disconnected: " + session.remoteHost + ":" + session.remotePort)
            sessions.remove(socket)
        }
    }

    @Synchronized fun receive(socket: WebSocket, jsonString: String) {
        val envelope = mapper.readValue(jsonString, Envelope::class.java)
        broadcast(envelope)
    }

    @Synchronized fun broadcast(envelope: Envelope) {
        val jsonString = mapper.writeValueAsString(envelope)
        broadcaster.broadcast(sessions.keys, jsonString)
    }


}