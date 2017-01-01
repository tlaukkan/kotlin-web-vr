package vr.webvr.tools

import vr.webvr.devices.InputButton
import vr.webvr.devices.InputDevice

/**
 * Created by tlaukkan on 11/1/2016.
 */
class AddTool(inputDevice: InputDevice) : Tool("Add Tool", inputDevice) {

    private enum class AddMode {
        PRIMITIVE, MODEL
    }

    private var mode: AddMode = AddMode.PRIMITIVE

    fun updateDisplay() {
        val text =
                    "$name\n" +
                    "Mode: $mode"
        inputDevice.display(text)
    }


    override fun active() {
        updateDisplay()
    }

    override fun deactive() {
    }

    override fun onPressed(button: InputButton) {
        println("Pressed: $button")
    }

    override fun onReleased(button: InputButton) {
        println("Released: $button")
        if (button == InputButton.RIGHT) {
            var newMode = mode.ordinal + 1
            if (newMode >= AddMode.values().size) {
                newMode = 0
            }
            mode = AddMode.values()[newMode]
            updateDisplay()
        }
        if (button == InputButton.LEFT) {
            var newMode = mode.ordinal - 1
            if (newMode < 0) {
                newMode = AddMode.values().size - 1
            }
            mode = AddMode.values()[newMode]
            updateDisplay()
        }
    }

    override fun onSqueezed(button: InputButton, value: Double) {
        println("Squeezed: $button $value")
    }

    override fun onPadTouched(x: Double, y: Double) {
        println("Pad touched: $x,$y")
    }

}