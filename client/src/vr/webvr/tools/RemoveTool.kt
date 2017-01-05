package vr.webvr.tools

import lib.threejs.*
import virtualRealityController
import vr.webvr.devices.InputButton
import vr.webvr.devices.InputDevice

/**
 * Created by tlaukkan on 11/1/2016.
 */
class RemoveTool(inputDevice: InputDevice) : Tool("RemoveTool Tool", inputDevice) {
    override fun active() {
        inputDevice.display("RemoveTool.")

        inputDevice.showSelectLine(0xff0000)
    }

    override fun render() {

    }

    override fun deactive() {
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
                val node = virtualRealityController!!.nodes[nodeUrl] ?: return
                val nodeType = virtualRealityController!!.nodeTypes[nodeUrl]!! ?: return
                node.removed = true
                virtualRealityController!!.networkClient!!.send(node, nodeType)
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