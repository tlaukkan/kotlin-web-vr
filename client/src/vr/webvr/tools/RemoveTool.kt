package vr.webvr.tools

import vr.CLIENT
import vr.webvr.devices.InputButton
import vr.webvr.devices.InputDevice

/**
 * Created by tlaukkan on 11/1/2016.
 */
class RemoveTool(inputDevice: InputDevice) : Tool("Remove", inputDevice) {
    override fun active() {
        inputDevice.display("RemoveTool.")

        inputDevice.showSelectLine(0xff0000)
    }

    override fun render() {

    }

    override fun deactivate() {
        inputDevice.hideSelectLine()
    }

    override fun onPressed(button: InputButton) {
        if (button == InputButton.TRIGGER) {
            inputDevice.unselectNodes()
            inputDevice.selectNodes()
        }
    }

    override fun onReleased(button: InputButton) {
        if (button == InputButton.TRIGGER) {
            if (inputDevice.selectedNodeUrls.size > 0) {
                val nodeUrl = inputDevice.selectedNodeUrls[0]
                val node = CLIENT!!.vrController!!.nodes[nodeUrl] ?: return
                val nodeType = CLIENT!!.vrController!!.nodeTypes[nodeUrl]!! ?: return
                node.removed = true
                CLIENT!!.vrController!!.networkClient!!.send(node, nodeType)
            }
        }
    }

    override fun onSqueezed(button: InputButton, value: Double) {
        if (button == InputButton.TRIGGER && value >= 1.0) {
            inputDevice.unselectNodes()
            inputDevice.selectNodes()
        }
    }

    override fun onPadTouched(x: Double, y: Double) {
    }

}