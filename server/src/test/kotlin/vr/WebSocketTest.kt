package vr

import logger
import vr.network.NetworkClient
import vr.util.Mapper
import vr.network.WsClient
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import vr.network.model.*
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
        NETWORK_SERVER.addCell(Cell("http://localhost:8080/api/cells/default"))
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
        var connected = false
        client.onOpen = {
            connected = true
        }

        client.connect()

        while (!connected) {
            Thread.sleep(10)
        }

        val handshakeRequest = HandshakeRequest("kotlin-web-vr", "vr-state-synchronisation", arrayOf("0.9", "1.0"))

        val original = Envelope()
        val values: MutableList<Any> = mutableListOf()
        values.addAll(listOf(handshakeRequest))
        mapper.writeValuesToEnvelope(original, values)

        client.send(mapper.writeValue(original))

        while (receivedMessages.size < 1) {
            Thread.sleep(100)
        }

        val receivedEnvelope = mapper.readValue(receivedMessages[0], Envelope::class.java)

        Assert.assertEquals("HandshakeResponse", receivedEnvelope.values[0].type)

        client.close()
    }

    @Test fun testNetworkClient() {
        val mapper = Mapper()

        var receivedValues = ArrayList<Any>()

        val client = NetworkClient("ws://localhost:8080/ws")
        client.onReceive = { value ->
            receivedValues.add(value)
        }

        var connected = false
        var handshakeResponse: HandshakeResponse? = null
        client.onConnected = { handshakeResponse_ ->
            connected = true
            handshakeResponse = handshakeResponse_
        }
        var cellSelected = false
        var linkedServerCellUri = ""
        client.onCellSelected = { cellSelectedResponse ->
            cellSelected = true
            linkedServerCellUri = cellSelectedResponse.serverCellUris[0]
        }
        client.onDisconnected = { reason ->
            log.log(Level.INFO, "WebSocket close: $reason")
            connected = false
        }

        log.info("Connecting...")
        while (!connected) {
            Thread.sleep(10)
        }

        log.info("Connected.")
        Assert.assertTrue(client.connected)
        Assert.assertNotNull(handshakeResponse)

        val firstServerCellUri = handshakeResponse!!.serverCellUris[0]
        log.info("Selecting first server cell URI $firstServerCellUri ...")
        client.send(listOf(LinkRequest(arrayOf(), arrayOf(firstServerCellUri))))

        while (!cellSelected) {
            Thread.sleep(10)
        }

        log.info("Selected cell.")
        Assert.assertTrue(client.cellSelected)
        Assert.assertEquals(firstServerCellUri, linkedServerCellUri)

        log.info("Sending node...")
        val node = Node(UUID.randomUUID().toString())
        node.url = "http://localhost:8080/api/cells/default/${node.id}"
        client.send(listOf(node))

        log.info("Waiting node broadcast...")
        while (true) {
            Thread.sleep(10)
            var found = false
            for (receivedValue in receivedValues) {
                //System.out.println(mapper.writeValue(node))
                //System.out.println(mapper.writeValue(receivedValue))
                if (mapper.writeValue(receivedValue).equals(mapper.writeValue(node))) {
                    found = true
                    break
                }
            }
            if (found) {
                break
            }
        }
        log.info("Received node broadcast")

        log.info("Disconnecting...")
        client.shutdown()

        while (connected) {
            Thread.sleep(10)
        }
        log.info("Disconnected.")

        Assert.assertFalse(client.connected)

    }
}