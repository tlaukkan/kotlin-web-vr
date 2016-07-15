package org.bubblecloud.webvr

import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft_17
import org.java_websocket.handshake.ServerHandshake
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.net.URI
import java.util.logging.LogManager

class WebSocketTest {

    private var server: RestServer? = null

    @Before fun setUp() {
        LogManager.getLogManager().readConfiguration(this.javaClass.getResourceAsStream("/logging.properties"))

        server = RestServer()
        server!!.startup()
    }

    @After fun tearDown() {
        server!!.shutdown()
    }

    @Test fun testWebSocket() {

        val clientEndPoint = object: WebSocketClient(URI("ws://localhost:8080/ws/echo"), Draft_17()) {

            override fun onMessage(message: String) {
                println(message)
            }

            override fun onOpen(handshake: ServerHandshake) {
                println("opened")
            }

            override fun onClose(code: Int, reason: String, remote: Boolean) {
                println("closed")
            }

            override fun onError(ex: Exception) {
                ex.printStackTrace()
            }
        }

        Assert.assertTrue(clientEndPoint.connectBlocking())

        clientEndPoint.send("test")

        Thread.sleep(1000)

        clientEndPoint.close()
    }

}