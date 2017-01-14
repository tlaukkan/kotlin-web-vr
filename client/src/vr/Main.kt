package vr

import lib.webvrapi.getVRDisplays
import lib.webvrapi.navigator
import vr.webvr.VrClient

var CLIENT: VrClient? = null

fun main(args: Array<String>) {
    println("Main begin...")

    navigator.getVRDisplays().then({ displays ->
        if (displays.size == 0) {
            println("VR display detected: none")
        } else {
            println("VR display detected: " + displays[0].displayName)
            CLIENT = VrClient(displays[0])
        }
    }).catch({ error ->
        println("Error getting VR displays: $error")
    })

    println("Main end.")
}

