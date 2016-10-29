package org.bubblecloud.webvr.util

import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft_17
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import java.nio.channels.NotYetConnectedException

class WsClient(url: String) {

    var onOpen: ((handshakedata: ServerHandshake?) -> Unit)? = null
    var onClose: ((code: Int, reason: String, remote: Boolean) -> Unit)? = null
    var onMessage: ((message: String) -> Unit)? = null
    var onError: ((ex: Exception) -> Unit)? = null

    val client = object : WebSocketClient(URI(url), Draft_17()) {
        override fun onOpen(handshakedata: ServerHandshake?) {
            synchronized(this) {
                if (onOpen != null) {
                    onOpen!!.invoke(handshakedata)
                }
            }
        }

        override fun onClose(code: Int, reason: String?, remote: Boolean) {
            synchronized(this) {
                if (onClose != null) {
                    onClose!!.invoke(code, reason!!, remote)
                }
            }
        }

        override fun onMessage(message: String?) {
            synchronized(this) {
                if (onMessage != null) {
                    onMessage!!.invoke(message!!)
                }
            }
        }

        override fun onError(ex: Exception?) {
            synchronized(this) {
                if (onError != null) {
                    onError!!.invoke(ex!!)
                }
            }
        }
    }

    fun connect(): Boolean {
        return client.connectBlocking()
    }

    fun send(text: String) {
        client.send(text)
    }


    fun close() {
        client.close()
    }

}