package vr.util

import lib.threejs.Quaternion
import lib.webvrapi.Float32Array
import java.util.*

fun floatsToDoubles(float32Array: Float32Array) : List<Double> {
    val stringArray = float32Array.toString().split(",")
    val doubleList = ArrayList<Double>()
    for (str in stringArray) {
        doubleList.add(safeParseDouble(str)!!)
    }
    return doubleList
}

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

/**
 * Dynamic cast to be able to cast JSON parsed objects to their type.
 */
fun <T> dynamicCast(obj: Any) : T {
    val dynamicNode: dynamic = obj
    return dynamicNode
}


fun getDeltaQuaternion(startOrientation: Quaternion, currentOrientation: Quaternion): Quaternion {
    val originalOrientationConjugate = startOrientation.conjugate()

    val orientationChange = currentOrientation.clone()
    orientationChange.multiply(originalOrientationConjugate)
    return orientationChange
}