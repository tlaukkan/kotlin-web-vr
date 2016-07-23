package org.bubblecloud.webvr

import logger
import org.bubblecloud.webvr.model.Envelope
import org.glassfish.grizzly.websockets.WebSocket
import java.util.*

class Transmitter() {
    private val log = logger()

    private val nodes: MutableMap<WebSocket, Session> = HashMap()

    fun addSession(session: Session) {
        log.info("Session connected: " + session.remoteHost + ":" + session.remotePort)
        nodes[session.socket] = session
    }

    fun removeSession(socket: WebSocket) {
        val session: Session? = nodes[socket]
        if (session != null) {
            log.info("Session disconnected: " + session.remoteHost + ":" + session.remotePort)
            nodes.remove(socket)
        }
    }

    fun broadcast(envelope: Envelope) {

    }

    fun receive(envelope: Envelope) {

    }

}