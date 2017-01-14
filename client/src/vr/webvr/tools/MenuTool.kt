package vr.webvr.tools

import vr.webvr.devices.InputButton
import vr.webvr.devices.InputDevice

/**
 * Created by tlaukkan on 11/1/2016.
 */
class MenuTool(inputDevice: InputDevice) : Tool("Menu Tool", inputDevice) {

    var toolIndex = 0

    fun updateDisplay() {
        var text = "$name\n"

        for (tool in inputDevice.tools) {
            val index = inputDevice.tools.indexOf(tool)
            if (index == toolIndex) {
                text += "$index) ${tool.name} <-\n"
            } else {
                text += "$index) ${tool.name} \n"
            }
        }
        inputDevice.display(text)
    }

    override fun active() {
        toolIndex = inputDevice.lastActiveToolIndex
        updateDisplay()
    }

    override fun render() {

    }

    override fun deactive() {
    }

    override fun onPressed(button: InputButton) {
    }

    override fun onReleased(button: InputButton) {
        if (button == InputButton.UP) {
            toolIndex -= 1
            if (toolIndex < 0) {
                toolIndex = inputDevice.tools.size - 1
            }
            updateDisplay()
        }
        if (button == InputButton.DOWN) {
            toolIndex ++
            if (toolIndex >= inputDevice.tools.size) {
                toolIndex = 0
            }
            updateDisplay()
        }
    }

    override fun onSqueezed(button: InputButton, value: Double) {
    }

    override fun onPadTouched(x: Double, y: Double) {
    }

}