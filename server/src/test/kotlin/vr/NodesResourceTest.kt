package vr

import vr.network.model.Node
import vr.network.RestClient

import org.junit.After
import org.junit.Before
import org.junit.Test

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import java.util.*
import java.util.logging.LogManager

class NodesResourceTest {

    private var server: VrServer = VrServer()
    private var client: RestClient<Node> = RestClient(server.url + "api", "nodes", Node::class.java)

    init {
        LogManager.getLogManager().readConfiguration(this.javaClass.getResourceAsStream("/logging.properties"))
    }

    @Before fun setUp() {
        server.startup()
    }

    @After fun tearDown() {
        server.shutdown()
    }

    @Test fun testNodeResources() {
        assertEquals(0, client.get().size)

        val nodeId: UUID = client.post(Node())

        val node = client.get(nodeId)

        assertNotNull(node)

        assertEquals(1, client.get().size)

        client.put(nodeId, node)

        client.delete(nodeId)

        assertEquals(0, client.get().size)
    }

}