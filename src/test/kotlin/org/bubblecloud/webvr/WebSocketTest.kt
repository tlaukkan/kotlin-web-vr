package org.bubblecloud.webvr

import com.fasterxml.jackson.databind.ObjectMapper
import logger
import org.bubblecloud.webvr.model.Envelope
import org.bubblecloud.webvr.model.Message
import org.bubblecloud.webvr.model.Node
import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft_17
import org.java_websocket.handshake.ServerHandshake
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.net.URI
import java.util.*
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

        var receivedMessages = ArrayList<String>()
        val clientEndPoint = object: WebSocketClient(URI("ws://localhost:8080/ws/echo"), Draft_17()) {
            override fun onOpen(handshake: ServerHandshake) {}
            override fun onClose(code: Int, reason: String, remote: Boolean) {}
            override fun onMessage(message: String) {
                receivedMessages.add(message)
            }
            override fun onError(ex: Exception) {
                log.log(Level.SEVERE, "WebSocket error.", ex)
            }
        }

        Assert.assertTrue(clientEndPoint.connectBlocking())

        val handshakeRequest = Message("handshake-request", mapOf(
                "software" to "kotlin-web-vr",
                "protocol-dialect" to "vr-state-synchronisation",
                "protocol-versions" to "1.0")
        )
        val original = Envelope()
        original.messages = listOf(handshakeRequest)
        original.nodes = listOf(Node())

        val mapper: ObjectMapper = ObjectMapper()
        val jsonString = mapper.writeValueAsString(original)
        clientEndPoint.send(jsonString)

        while (receivedMessages.size < 2) {
            Thread.sleep(100)
        }

        val receivedEnvelope = mapper.readValue(receivedMessages[1], Envelope::class.java)

        Assert.assertEquals(original.nodes, receivedEnvelope.nodes)

        clientEndPoint.close()
    }

}