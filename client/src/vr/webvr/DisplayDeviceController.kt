package vr.webvr

import lib.webvrapi.VRDisplay
import lib.webvrapi.getVRDisplays
import lib.webvrapi.navigator

/**
 * Created by tlaukkan on 10/26/2016.
 */
class DisplayDeviceController {

    var display: VRDisplay? = null

    fun startup(then: () -> Unit, error: (message: String) -> Unit) {
        try {
            navigator.getVRDisplays().catch({ error ->
                error("Error getting VR displays: $error")
            }).then({ displays ->
                if (displays.size == 0) {
                    println("No VR display detected.")
                    error("No VR display detected.")
                } else {
                    display = displays[0]
                    println("VR display detected: " + display!!.displayName)
                    then()
                }
            })
        } catch (t: Throwable) {
            println("WebVR API is not supported by your browser.")
            error("WebVR API is not supported by your browser.")
        }
    }


}