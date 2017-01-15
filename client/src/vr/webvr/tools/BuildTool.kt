package vr.webvr.tools

import lib.threejs.Quaternion
import lib.threejs.Vector3
import vr.CLIENT
import vr.util.getDeltaQuaternion
import vr.webvr.devices.InputButton
import vr.webvr.devices.InputDevice

/**
 * Created by tlaukkan on 11/1/2016.
 */
class BuildTool(inputDevice: InputDevice) : Tool("Build", inputDevice) {

    override fun active() {
        gripped = false
        inputDevice.showSelectLine(0xffa500)
    }

    override fun render() {

    }

    override fun deactivate() {
        gripped = false
        inputDevice.unselectNodes()
        inputDevice.hideSelectLine()
    }

    override fun onPressed(button: InputButton) {
        if (button == InputButton.GRIP) {
            gripped = true
        }

    }

    override fun onReleased(button: InputButton) {
        if (button == InputButton.GRIP) {
            gripped = false
        }

        if (button == InputButton.TRIGGER) {
            if (inputDevice.selectNodes() == null) {
                inputDevice.unselectNodes()
            }
        }

        if (!gripped) {
            if (button == InputButton.UP) {
                translate(Vector3(0.0, 0.0, 1.0))
            }

            if (button == InputButton.DOWN) {
                translate(Vector3(0.0, 0.0, -1.0))
            }

            if (button == InputButton.LEFT) {
                translate(Vector3(1.0, 0.0, 0.0))
            }

            if (button == InputButton.RIGHT) {
                translate(Vector3(-1.0, 0.0, 0.0))
            }
        } else {
            if (button == InputButton.UP) {
                translate(Vector3(0.0, 1.0, 0.0))
            }

            if (button == InputButton.DOWN) {
                translate(Vector3(0.0, -1.0,0.0))
            }
        }
    }

    override fun onSqueezed(button: InputButton, value: Double) {
        println("Squeezed: $button $value")
    }

    override fun onPadTouched(x: Double, y: Double) {
        println("Pad touched: $x,$y")
    }

    private fun translate(translation: Vector3) {

        for (nodeUrl in inputDevice.selectedNodeUrls) {
            val node = CLIENT!!.vrController.nodes[nodeUrl] ?: return
            val nodeType = CLIENT!!.vrController.nodeTypes[nodeUrl] ?: return

            var obj = CLIENT!!.vrController.scene.getObjectByName(node.url) ?: return

            val objectOrientation = Quaternion(0.0, 0.0, 0.0, 1.0)
            obj.getWorldQuaternion(objectOrientation)
            translation.applyQuaternion(objectOrientation)

            val x = node.position.x
            val y = node.position.y
            val z = node.position.z

            node.position.x += translation.x * node.scale.x
            node.position.y += translation.y * node.scale.y
            node.position.z += translation.z * node.scale.z

            CLIENT!!.vrController.networkClient!!.send(node, nodeType)

            node.position.x = x
            node.position.y = y
            node.position.z = z
        }

    }
}