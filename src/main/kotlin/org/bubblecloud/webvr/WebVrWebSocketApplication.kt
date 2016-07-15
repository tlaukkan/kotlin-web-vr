package org.bubblecloud.webvr

import org.glassfish.grizzly.http.HttpRequestPacket
import org.glassfish.grizzly.websockets.DataFrame
import org.glassfish.grizzly.websockets.HandShake
import org.glassfish.grizzly.websockets.WebSocket
import org.glassfish.grizzly.websockets.WebSocketApplication

/**
 * Created by tlaukkan on 7/15/2016.
 */
class WebVrWebSocketApplication : WebSocketApplication() {

    override fun onConnect(socket: WebSocket?) {
        super.onConnect(socket)
    }

    override fun onClose(socket: WebSocket?, frame: DataFrame?) {
        super.onClose(socket, frame)
    }

    override fun onError(webSocket: WebSocket?, t: Throwable?): Boolean {
        return super.onError(webSocket, t)
    }

    override fun onMessage(socket: WebSocket?, text: String?) {
        super.onMessage(socket, text)
        socket!!.send(text)
    }

    override fun isApplicationRequest(request: HttpRequestPacket?): Boolean {
        return super.isApplicationRequest(request)
    }

    override fun handshake(handshake: HandShake?) {
        super.handshake(handshake)
    }
}