package org.bubblecloud.webvr

import org.bubblecloud.webvr.model.Envelope
import org.bubblecloud.webvr.model.Node
import org.bubblecloud.webvr.util.Mapper
import org.junit.Test

import org.junit.Assert.assertEquals

class JsonSerializationTest {

    @Test fun testEnvelopeJsonSerialization() {
        val mapper: Mapper = Mapper()
        val original = Envelope()
        mapper.writeValuesToEnvelope(original, listOf(Node()))
        val jsonString = mapper.writeValue(original)
        println(jsonString)
        val parsed = mapper.readValue(jsonString, Envelope::class.java)
        assertEquals(original.toString(), parsed.toString())
    }

}