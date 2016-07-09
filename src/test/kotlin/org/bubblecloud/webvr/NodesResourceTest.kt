package org.bubblecloud.webvr

import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.client.WebTarget

import org.junit.After
import org.junit.Before
import org.junit.Test

import org.junit.Assert.assertEquals

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
        val node = target!!.path("nodes").request().get(NodeList::class.java)[0]
        println(node)
        assertEquals(1, node.id.toLong())
    }

}