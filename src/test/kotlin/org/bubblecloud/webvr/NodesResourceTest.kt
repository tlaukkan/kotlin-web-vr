package org.bubblecloud.webvr

import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.client.WebTarget

import org.junit.After
import org.junit.Before
import org.junit.Test

import org.junit.Assert.assertEquals
import java.util.*
import javax.ws.rs.client.Entity
import javax.ws.rs.core.GenericType
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

class NodesResourceTest {

    private var server: RestServer? = null
    private var target: WebTarget? = null

    @Before fun setUp() {
        server = RestServer()
        val c = ClientBuilder.newClient()
        target = c.target(server!!.url)
    }

    @After fun tearDown() {
        server!!.shutdown()
    }

    @Test fun testGetNodes() {
        val node = target!!.path("nodes").request().get(object : GenericType<List<Node>>() {

        })[0]
        println(node)
        //assertEquals("a6d5a059-5e03-4b87-bb48-9a28a5f5d7d0", node.id)
    }

    @Test fun testGetNode() {
        val node = target!!.path("nodes/a6d5a059-5e03-4b87-bb48-9a28a5f5d7d0").request().get(object : GenericType<Node>() {

        })
        println(node)
        //assertEquals(2, node.id.toLong())
    }

    @Test fun testCreateNode() {
        val response: Response = target!!.path("nodes").request().post(Entity.entity(Node(), MediaType.APPLICATION_JSON))
        assertEquals(201, response.status)
       // assertEquals(server!!.url + "nodes/a6d5a059-5e03-4b87-bb48-9a28a5f5d7d0", response.location.toString())

    }

    @Test fun testUpdateNode() {
        target!!.path("nodes/a6d5a059-5e03-4b87-bb48-9a28a5f5d7d0").request().put(Entity.entity(Node(UUID.fromString(
                "a6d5a059-5e03-4b87-bb48-9a28a5f5d7d0")), MediaType.APPLICATION_JSON))
    }

    @Test fun testDeleteNode() {
        target!!.path("nodes/a6d5a059-5e03-4b87-bb48-9a28a5f5d7d0").request().delete()
    }

}