package org.bubblecloud.webvr.util

import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft_17
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import java.nio.channels.NotYetConnectedException

class WsClient(url: String) {

    var onOpenHandler: ((handshakedata: ServerHandshake?) -> Unit)? = null
    var onCloseHandler: ((code: Int, reason: String, remote: Boolean) -> Unit)? = null
    var onMessage: ((message: String) -> Unit)? = null
    var onError: ((ex: Exception) -> Unit)? = null

    val client = object : WebSocketClient(URI(url), Draft_17()) {
        override fun onOpen(handshakedata: ServerHandshake?) {
            synchronized(this) {
                if (onOpenHandler != null) {
                    onOpenHandler!!.invoke(handshakedata)
                }
            }
        }

        override fun onClose(code: Int, reason: String?, remote: Boolean) {
            synchronized(this) {
                if (onCloseHandler != null) {
                    onCloseHandler!!.invoke(code, reason!!, remote)
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

    @Throws(InterruptedException::class)
    fun connect(): Boolean {
        return client.connectBlocking()
    }

    @Throws(NotYetConnectedException::class)
    fun send(text: String) {
        client.send(text)
    }


    fun close() {
        client.close()
    }

}