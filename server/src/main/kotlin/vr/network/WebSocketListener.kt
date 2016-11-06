package vr.network

import logger
import vr.model.Session
import org.glassfish.grizzly.websockets.*
import vr.PORT_NETWORK_SERVER_MAP
import java.net.URL
import java.util.*
import java.util.logging.Level

/**
 * Created by tlaukkan on 7/15/2016.
 */
class WebSocketListener() : WebSocketApplication() {

    private val log = logger()
    val socketServerMap: MutableMap<WebSocket, NetworkServer> = HashMap()

    init {
        WebSocketEngine.getEngine().register("", "/ws", this)
    }

    override fun onConnect(socket: WebSocket?) {
        super.onConnect(socket)
        val defaultWebSocket = (socket as DefaultWebSocket)
        val port = URL(defaultWebSocket.upgradeRequest.requestURL.toString()).port

        val remoteHost = defaultWebSocket.upgradeRequest.remoteHost
        val remotePort = socket.upgradeRequest.remotePort

        val networkServer = PORT_NETWORK_SERVER_MAP[port]!!
        socketServerMap[socket] = networkServer
        networkServer.addSession(Session(remoteHost, remotePort, socket))
    }

    override fun onClose(socket: WebSocket?, frame: DataFrame?) {
        socket!!
        super.onClose(socket, frame)
        socketServerMap[socket]!!.removeSession(socket)
        socketServerMap.remove(socket)
    }

    override fun onError(socket: WebSocket?, t: Throwable?): Boolean {
        val session: Session? = socketServerMap[socket]!!.getSession(socket)
        if (session != null) {
            log.log(Level.SEVERE, "Error in web socket communication: ${session.remoteHost} : ${session.remotePort}", t)
        } else {
            log.log(Level.SEVERE, "Error in web socket communication.", t)
        }
        return super.onError(socket, t)
    }

    override fun onMessage(socket: WebSocket?, text: String?) {
        super.onMessage(socket, text)
        socketServerMap[socket]!!.receive(socket!!, text!!)
    }

    override fun handshake(handshake: HandShake?) {
        super.handshake(handshake)
    }
}