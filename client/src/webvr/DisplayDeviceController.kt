package webvr

import org.w3c.dom.Node
import webvrapi.VRDisplay
import webvrapi.getVRDisplays
import webvrapi.navigator
import kotlin.browser.document
import kotlin.browser.window
import kotlin.dom.addClass
import kotlin.dom.onClick

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