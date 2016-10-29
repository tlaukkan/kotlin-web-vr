package org.bubblecloud.webvr

import logger
import org.bubblecloud.webvr.model.Session
import org.glassfish.grizzly.websockets.*
import java.util.logging.Level

/**
 * Created by tlaukkan on 7/15/2016.
 */
class WebSocketListener : WebSocketApplication() {

    private val log = logger()

    override fun onConnect(socket: WebSocket?) {
        super.onConnect(socket)
        val remoteHost = ((socket as DefaultWebSocket).upgradeRequest).remoteHost
        val remotePort = socket.upgradeRequest.remotePort
        NETWORK_SERVER.addSession(Session(remoteHost, remotePort, socket))
    }

    override fun onClose(socket: WebSocket?, frame: DataFrame?) {
        socket!!
        super.onClose(socket, frame)
        NETWORK_SERVER.removeSession(socket)
    }

    override fun onError(webSocket: WebSocket?, t: Throwable?): Boolean {
        val session: Session? = NETWORK_SERVER.getSession(webSocket)
        if (session != null) {
            log.log(Level.SEVERE, "Error in web socket communication: ${session.remoteHost} : ${session.remotePort}", t)
        } else {
            log.log(Level.SEVERE, "Error in web socket communication.", t)
        }
        return super.onError(webSocket, t)
    }

    override fun onMessage(socket: WebSocket?, text: String?) {
        super.onMessage(socket, text)
        NETWORK_SERVER.receive(socket!!, text!!)
    }

    override fun handshake(handshake: HandShake?) {
        super.handshake(handshake)
    }
}