package org.bubblecloud.webvr

import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.client.WebTarget

import org.junit.After
import org.junit.Before
import org.junit.Test

import org.junit.Assert.assertEquals
import javax.ws.rs.client.Entity
import javax.ws.rs.core.GenericEntity
import javax.ws.rs.core.GenericType
import javax.ws.rs.core.MediaType

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

    @Test fun testPostNodes() {
        target!!.path("nodes").request().post(Entity.entity(listOf(Node(1)), MediaType.APPLICATION_JSON))
    }

}