package org.bubblecloud.webvr

import logger
import org.bubblecloud.webvr.model.Envelope
import org.bubblecloud.webvr.model.HandshakeRequest
import org.bubblecloud.webvr.model.Node
import org.bubblecloud.webvr.util.Mapper
import org.bubblecloud.webvr.util.WsClient
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.*
import java.util.logging.Level
import java.util.logging.LogManager

class WebSocketTest {
    private val log = logger()

    private var server: VrServer = VrServer()

    init {
        LogManager.getLogManager().readConfiguration(this.javaClass.getResourceAsStream("/logging.properties"))
    }

    @Before fun setUp() {
        server.startup()
    }

    @After fun tearDown() {
        server.shutdown()
    }

    @Test fun testWebSocket() {
        val mapper = Mapper()

        var receivedMessages = ArrayList<String>()

        val client = WsClient("ws://localhost:8080/ws")
        client.onMessage = { message ->
            receivedMessages.add(message)
        }
        client.onError = { e ->
            log.log(Level.SEVERE, "WebSocket error.", e)
        }

        Assert.assertTrue(client.connect())

        val handshakeRequest = HandshakeRequest("kotlin-web-vr", "vr-state-synchronisation", arrayOf("0.9", "1.0"))

        val originalNodes = listOf(Node())
        val original = Envelope()
        val values: MutableList<Any> = mutableListOf()
        values.addAll(listOf(handshakeRequest))
        values.addAll(originalNodes)
        mapper.writeValuesToEnvelope(original, values)

        client.send(mapper.writeValue(original))

        while (receivedMessages.size < 2) {
            Thread.sleep(100)
        }

        val receivedEnvelope = mapper.readValue(receivedMessages[1], Envelope::class.java)
        val receivedValues = mapper.readValuesFromEnvelope(receivedEnvelope)

        Assert.assertEquals(originalNodes, receivedValues)

        client.close()
    }

}