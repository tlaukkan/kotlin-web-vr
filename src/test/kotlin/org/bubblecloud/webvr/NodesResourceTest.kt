package org.bubblecloud.webvr

import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.client.WebTarget

import org.junit.After
import org.junit.Before
import org.junit.Test

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import java.io.FileInputStream
import java.net.URI
import java.util.*
import java.util.logging.LogManager
import javax.ws.rs.client.Entity
import javax.ws.rs.core.GenericType
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

class NodesResourceTest {

    private var server: RestServer? = null
    private var target: WebTarget? = null

    @Before fun setUp() {
        LogManager.getLogManager().readConfiguration(this.javaClass.getResourceAsStream("/logging.properties"))

        server = RestServer()
        val c = ClientBuilder.newClient()
        target = c.target(server!!.url)
    }

    @After fun tearDown() {
        server!!.shutdown()
    }

    @Test fun testNodeResources() {

        assertEquals(0, target!!.path("nodes").request().get(object : GenericType<List<Node>>() {
        }).size)

        val response: Response = target!!.path("nodes").request().post(Entity.entity(Node(), MediaType.APPLICATION_JSON))
        assertEquals(response.status, Response.Status.CREATED.statusCode)
        val nodeUri: URI = response.location
        val nodeId: UUID = response.readEntity(UUID::class.java)
        assertNotNull(nodeUri)

        val node = target!!.path("nodes/$nodeId").request().get(object : GenericType<Node>() {
        })

        assertNotNull(node)

        assertEquals(1, target!!.path("nodes").request().get(object : GenericType<List<Node>>() {
        }).size)

        target!!.path("nodes/$nodeId").request().put(Entity.entity(node, MediaType.APPLICATION_JSON))

        target!!.path("nodes/$nodeId").request().delete()

        assertEquals(0, target!!.path("nodes").request().get(object : GenericType<List<Node>>() {
        }).size)

    }

}