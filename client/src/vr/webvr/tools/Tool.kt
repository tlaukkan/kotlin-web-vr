package vr.webvr.tools

import vr.webvr.devices.InputButton
import vr.webvr.devices.InputDevice
import kotlin.browser.window

/**
 * Created by tlaukkan on 11/1/2016.
 */
abstract class Tool(var name: String, var inputDevice: InputDevice) {

    fun activate() {
        inputDevice.onPressed = { button ->
            onPressed(button)
        }
        inputDevice.onReleased = { button ->
            onReleased(button)
        }
        inputDevice.onSqueezed = { button, value ->
            onSqueezed(button, value)
        }
        inputDevice.onPadTouched = { x, y ->
            onPadTouched(x, y)
        }
        inputDevice.display(name)
    }

    abstract fun onPressed(button: InputButton)

    abstract fun onReleased(button: InputButton)

    abstract fun onSqueezed(button: InputButton, value: Double)

    abstract fun onPadTouched(x: Double, y: Double)
    
}