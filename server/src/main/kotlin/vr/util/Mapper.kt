package vr.util

import com.fasterxml.jackson.databind.ObjectMapper
import logger
import vr.network.model.*
import java.util.logging.Level

class Mapper {

    private val log = logger()
    val valueClasses = mutableMapOf<String, Any>()
    val mapper = ObjectMapper()

    init {
        addValueClass(Node::class.java)
        addValueClass(LightFieldNode::class.java)
        addValueClass(ModelNode::class.java)
        addValueClass(HandshakeRequest::class.java)
        addValueClass(HandshakeResponse::class.java)
        addValueClass(CellSelectRequest::class.java)
        addValueClass(CellSelectResponse::class.java)
    }

    fun addValueClass(valueClass: Any): Unit {
        valueClasses[(valueClass as Class<Any>).simpleName] = valueClass
    }

    fun readValuesFromEnvelope(envelope: Envelope): List<Any> {
        val values: MutableList<Any> = mutableListOf()
        for (typedValue in envelope.values) {
            if (valueClasses.containsKey(typedValue.type)) {
                val valueClass = valueClasses[typedValue.type]
                values.add(mapper.readValue(typedValue.json, valueClass as Class<Any>))
            } else {
                log.log(Level.WARNING, "Unknown value type in envelope: ${typedValue.type}")
            }
        }
        return values
    }

    fun <T : Any> writeValuesToEnvelope(envelope: Envelope, values: List<T>): Unit {
        val typedValues: MutableList<TypedValue> = mutableListOf()
        for (value in values) {
            val type = value.javaClass.simpleName
            typedValues.add(TypedValue(type, 1, mapper.writeValueAsString(value)))
        }
        envelope.values = typedValues.toTypedArray()
    }

    fun <T> readValue(content: String, valueType: Class<T>): T {
        return mapper.readValue(content, valueType)
    }

    fun writeValue(value: Any): String {
        return mapper.writeValueAsString(value)
    }
}