package vr

import logger
import vr.network.model.Envelope
import vr.network.model.HandshakeRequest
import vr.network.model.Node
import vr.network.NetworkClient
import vr.util.Mapper
import vr.network.WsClient
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

    @Test fun testWsClient() {
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

    @Test fun testNetworkClient() {
        val mapper = Mapper()

        var received = ArrayList<Any>()

        val client = NetworkClient("ws://localhost:8080/ws")
        client.onReceive = { value ->
            received.add(value)
        }

        var connected = false
        client.onConnected = {
            connected = true
        }
        client.onDisconnected = { reason ->
            log.log(Level.INFO, "WebSocket close: $reason")
            connected = false
        }

        log.info("Connecting...")
        Assert.assertTrue(client.startup())

        log.info("Handshaking...")
        while (!connected) {
            Thread.sleep(10)
        }

        log.info("Connected.")
        Assert.assertTrue(client.connected)

        log.info("Sending node...")
        val node = Node()
        client.send(listOf(node))

        log.info("Waiting node broadcast...")
        while (received.size < 1) {
            Thread.sleep(10)
        }
        log.info("Received node broadcast")


        Assert.assertEquals(node, received[0])

        log.info("Disconnecting...")
        client.shutdown()

        while (connected) {
            Thread.sleep(10)
        }
        log.info("Disconnected.")

        Assert.assertFalse(client.connected)

    }
}