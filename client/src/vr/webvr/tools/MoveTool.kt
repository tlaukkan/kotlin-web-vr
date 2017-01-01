package vr.webvr.tools

import lib.threejs.*
import virtualRealityController
import vr.webvr.devices.InputButton
import vr.webvr.devices.InputDevice

/**
 * Created by tlaukkan on 11/1/2016.
 */
class MoveTool(inputDevice: InputDevice) : Tool("No Tool", inputDevice) {
    override fun active() {
        inputDevice.display("No tool selected.\nUse menu key.")

        inputDevice.showSelectLine()
    }

    override fun deactive() {
        inputDevice.hideSelectLine()
    }

    override fun onPressed(button: InputButton) {
        if (button == InputButton.GRIP) {
            gripped = true
        }

        if (!gripped) {
            inputDevice.unselectNodes()
        }
        inputDevice.selectNodes()
    }

    override fun onReleased(button: InputButton) {
        if (button == InputButton.GRIP) {
            gripped = false
        }
    }

    override fun onSqueezed(button: InputButton, value: Double) {
    }

    override fun onPadTouched(x: Double, y: Double) {
    }

}