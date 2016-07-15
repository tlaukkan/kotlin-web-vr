package org.bubblecloud.webvr

import com.fasterxml.jackson.databind.ObjectMapper
import org.bubblecloud.webvr.model.Envelope
import org.bubblecloud.webvr.model.Node
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

class JsonSerializationTest {

    @Test fun testEnvelopeJsonSerialization() {
        val mapper: ObjectMapper = ObjectMapper()
        val original = Envelope()
        original.nodes = listOf(Node())
        val jsonString = mapper.writeValueAsString(original)
        println(jsonString)
        val parsed = mapper.readValue(jsonString, Envelope::class.java)
        assertEquals(original.toString(), parsed.toString())
    }

}