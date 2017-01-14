package vr

import vr.network.model.Node
import vr.network.RestClient

import org.junit.After
import org.junit.Before
import org.junit.Test

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import vr.server.VrServer
import vr.server.model.Cell
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
        server.networkServer.addCell(Cell("http://localhost:8080/api/cells/default"))
    }

    @After fun tearDown() {
        server.shutdown()
    }

    @Test fun testNodeResources() {
        /*assertEquals(0, client.get().size)

        val uri: String = client.post(Node())

        val node = client.get(uri)

        assertNotNull(node)

        assertEquals(1, client.get().size)

        client.put(uri, node)

        client.delete(uri)

        assertEquals(0, client.get().size)*/
    }

}