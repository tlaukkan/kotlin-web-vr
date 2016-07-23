package org.bubblecloud.webvr

import org.glassfish.grizzly.http.HttpRequestPacket
import org.glassfish.grizzly.websockets.*

/**
 * Created by tlaukkan on 7/15/2016.
 */
class WebVrWebSocketApplication : WebSocketApplication() {

    override fun onConnect(socket: WebSocket?) {
        super.onConnect(socket)
        val remoteHost = ((socket as DefaultWebSocket).upgradeRequest).remoteHost
        val remotePort = socket.upgradeRequest.remotePort
        println(remoteHost)
        println(remotePort)
        TRANSMITTER.addSession(Session(remoteHost, remotePort, socket))
    }

    override fun onClose(socket: WebSocket?, frame: DataFrame?) {
        socket!!
        super.onClose(socket, frame)
        TRANSMITTER.removeSession(socket)
    }

    override fun onError(webSocket: WebSocket?, t: Throwable?): Boolean {
        return super.onError(webSocket, t)
    }

    override fun onMessage(socket: WebSocket?, text: String?) {
        super.onMessage(socket, text)
        socket!!.send(text)
        println(((socket as DefaultWebSocket).upgradeRequest).remoteHost)
        println(((socket as DefaultWebSocket).upgradeRequest).remotePort)
    }

    override fun isApplicationRequest(request: HttpRequestPacket?): Boolean {
        return super.isApplicationRequest(request)
    }

    override fun handshake(handshake: HandShake?) {
        super.handshake(handshake)
    }
}