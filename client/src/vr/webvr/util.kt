package vr.webvr

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