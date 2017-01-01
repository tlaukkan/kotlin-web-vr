package vr.webvr.tools

import lib.threejs.*
import virtualRealityController
import vr.webvr.devices.InputButton
import vr.webvr.devices.InputDevice

/**
 * Created by tlaukkan on 11/1/2016.
 */
class SelectTool(inputDevice: InputDevice) : Tool("No Tool", inputDevice) {
    override fun active() {
        inputDevice.display("Select tool.\nUse menu key to change tool.")

        inputDevice.showSelectLine()
    }

    override fun deactive() {
        inputDevice.hideSelectLine()
        gripped = false
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