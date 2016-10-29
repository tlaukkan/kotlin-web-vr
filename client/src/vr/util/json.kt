package vr.util

/**
 * Writes given plain data object containing only primitives, arrays and other similar data objects as JSON string.
 */
fun toJson(value: Any): String {
    return JSON.stringify(value)
}

/**
 * Parses JSON string to data object containing only primitives, arrays and other similar data objects.
 */
fun <T : Any> fromJson(string: String): T {
    return JSON.parse(string)
}