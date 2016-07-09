package org.bubblecloud.webvr

import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.client.WebTarget

import org.junit.After
import org.junit.Before
import org.junit.Test

import org.junit.Assert.assertEquals
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
        assertEquals(1, node.id.toLong())
    }

    @Test fun testGetNode() {
        val node = target!!.path("nodes/2").request().get(object : GenericType<Node>() {

        })
        println(node)
        assertEquals(2, node.id.toLong())
    }

    @Test fun testCreateNode() {
        val response: Response = target!!.path("nodes").request().post(Entity.entity(Node(1), MediaType.APPLICATION_JSON))
        assertEquals(201, response.status)
        assertEquals(server!!.url + "nodes/3", response.location.toString())

    }

    @Test fun testUpdateNode() {
        target!!.path("nodes/4").request().put(Entity.entity(Node(4), MediaType.APPLICATION_JSON))
    }

    @Test fun testDeleteNode() {
        target!!.path("nodes/5").request().delete()
    }

}