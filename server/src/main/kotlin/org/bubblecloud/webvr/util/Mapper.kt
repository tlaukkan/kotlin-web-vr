package org.bubblecloud.webvr.util

import com.fasterxml.jackson.databind.ObjectMapper
import org.bubblecloud.webvr.model.Envelope
import org.bubblecloud.webvr.model.Message
import org.bubblecloud.webvr.model.Node
import org.bubblecloud.webvr.model.TypedValue

class Mapper {

    val valueClasses = mutableMapOf<String, Any>()
    val mapper = ObjectMapper()

    init {
        addValueClass(Node::class.java)
        addValueClass(Message::class.java)
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