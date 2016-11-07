package vr.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectWriter
import logger
import vr.network.model.*
import java.util.logging.Level

class Mapper {

    private val log = logger()
    val valueClasses = mutableMapOf<String, Any>()
    val mapper = ObjectMapper()
    val prettyWriter = mapper.writerWithDefaultPrettyPrinter<ObjectWriter>()

    init {
        addValueClass(Node::class.java)
        addValueClass(LightFieldNode::class.java)
        addValueClass(ModelNode::class.java)
        addValueClass(PrimitiveNode::class.java)
        addValueClass(HandshakeRequest::class.java)
        addValueClass(HandshakeResponse::class.java)
        addValueClass(LinkRequest::class.java)
        addValueClass(LinkResponse::class.java)
    }

    fun addValueClass(valueClass: Any): Unit {
        valueClasses[(valueClass as Class<Any>).simpleName] = valueClass
    }

    fun readValuesFromEnvelope(envelope: Envelope): List<Any> {
        val values: MutableList<Any> = mutableListOf()
        for (typedValue in envelope.values) {
            val json = typedValue.json
            val type = typedValue.type
            val value: Any? = readValue(json, type)
            if (value != null) {
                values.add(value)
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

    fun writeValue(value: Any, pretty: Boolean = false): String {
        if (pretty) {
            return prettyWriter.writeValueAsString(value)
        } else {
            return mapper.writeValueAsString(value)
        }
    }

    fun getValueType(value: Any): String {
        return value.javaClass.simpleName
    }

    fun readValue(json: String, type: String): Any? {
        if (valueClasses.containsKey(type)) {
            val valueClass = valueClasses[type]
            return mapper.readValue(json, valueClass as Class<Any>)
        } else {
            log.log(Level.WARNING, "Read value from JSON failed. Unknown value type: ${type}")
            return null
        }
    }
}