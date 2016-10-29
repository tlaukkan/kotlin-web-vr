package org.bubblecloud.webvr

import com.fasterxml.jackson.databind.ObjectMapper
import logger
import org.bubblecloud.webvr.model.Envelope
import org.bubblecloud.webvr.model.Message
import org.bubblecloud.webvr.model.Node
import org.bubblecloud.webvr.util.Mapper
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

    private var server: VrServer? = null

    @Before fun setUp() {
        LogManager.getLogManager().readConfiguration(this.javaClass.getResourceAsStream("/logging.properties"))

        server = VrServer()
        server!!.startup()
    }

    @After fun tearDown() {
        server!!.shutdown()
    }

    @Test fun testWebSocket() {
        val mapper = Mapper()

        var receivedMessages = ArrayList<String>()
        val clientEndPoint = object: WebSocketClient(URI("ws://localhost:8080/ws"), Draft_17()) {
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
                "protocol-versions" to listOf("0.9", "1.0"))
        )

        val originalNodes = listOf(Node())
        val original = Envelope()
        val values: MutableList<Any> = mutableListOf()
        values.addAll(listOf(handshakeRequest))
        values.addAll(originalNodes)
        mapper.writeValuesToEnvelope(original, values)

        val jsonString = mapper.writeValue(original)
        clientEndPoint.send(jsonString)

        while (receivedMessages.size < 2) {
            Thread.sleep(100)
        }

        val receivedEnvelope = mapper.readValue(receivedMessages[1], Envelope::class.java)
        val receivedValues = mapper.readValuesFromEnvelope(receivedEnvelope)

        Assert.assertEquals(originalNodes, receivedValues)

        clientEndPoint.close()
    }

}