package vr.webvr.tools

import vr.webvr.devices.InputButton
import vr.webvr.devices.InputDevice

/**
 * Created by tlaukkan on 11/1/2016.
 */
class MoveTool(inputDevice: InputDevice) : Tool("Move Tool", inputDevice) {

    override fun active() {
    }

    override fun deactive() {
    }

    override fun onPressed(button: InputButton) {
        println("Pressed: $button")
    }

    override fun onReleased(button: InputButton) {
        println("Released: $button")
    }

    override fun onSqueezed(button: InputButton, value: Double) {
        println("Squeezed: $button $value")
    }

    override fun onPadTouched(x: Double, y: Double) {
        println("Pad touched: $x,$y")
    }

}