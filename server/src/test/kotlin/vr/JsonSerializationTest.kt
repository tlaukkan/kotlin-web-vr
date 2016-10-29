package vr

import vr.network.model.Envelope
import vr.network.model.Node
import vr.util.Mapper
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