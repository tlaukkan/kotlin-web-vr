package network

import network.model.*
import util.fromJson
import util.toJson

class Mapper {

    fun readValuesFromEnvelope(envelope: Envelope): List<Pair<String, Any>> {
        val values: MutableList<Pair<String, Any>> = mutableListOf()
        for (typedValue in envelope.values) {
            values.add(Pair(typedValue.type,fromJson(typedValue.json)))
        }
        return values
    }

    fun <T : Any> writeValuesToEnvelope(envelope: Envelope, values: List<T>): Unit {
        val typedValues: MutableList<TypedValue> = mutableListOf()
        for (value in values) {
            val type = value.jsClass.name
            typedValues.add(TypedValue(type, 1, toJson(value)))
        }
        envelope.values = typedValues.toTypedArray()
    }

    fun <T : Any> readValue(content: String): T {
        return fromJson(content)
    }

    fun writeValue(value: Any): String {
        return toJson(value)
    }
}