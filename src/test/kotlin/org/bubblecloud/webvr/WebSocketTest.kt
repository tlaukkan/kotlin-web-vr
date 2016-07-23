package org.bubblecloud.webvr

import com.fasterxml.jackson.databind.ObjectMapper
import logger
import org.bubblecloud.webvr.model.Envelope
import org.bubblecloud.webvr.model.Node
import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft_17
import org.java_websocket.handshake.ServerHandshake
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.net.URI
import java.util.logging.Level
import java.util.logging.LogManager

class WebSocketTest {
    private val log = logger()

    private var server: Server? = null

    @Before fun setUp() {
        LogManager.getLogManager().readConfiguration(this.javaClass.getResourceAsStream("/logging.properties"))

        server = Server()
        server!!.startup()
    }

    @After fun tearDown() {
        server!!.shutdown()
    }

    @Test fun testWebSocket() {

        val clientEndPoint = object: WebSocketClient(URI("ws://localhost:8080/ws/echo"), Draft_17()) {

            override fun onMessage(message: String) {
                log.info(message)
            }

            override fun onOpen(handshake: ServerHandshake) {
            }

            override fun onClose(code: Int, reason: String, remote: Boolean) {
            }

            override fun onError(ex: Exception) {
                log.log(Level.SEVERE, "WebSocket error.", ex)
                ex.printStackTrace()
            }
        }

        Assert.assertTrue(clientEndPoint.connectBlocking())

        val original = Envelope()
        original.nodes = listOf(Node())

        val mapper: ObjectMapper = ObjectMapper()
        val jsonString = mapper.writeValueAsString(original)
        clientEndPoint.send(jsonString)

        Thread.sleep(1000)

        clientEndPoint.close()
    }

}