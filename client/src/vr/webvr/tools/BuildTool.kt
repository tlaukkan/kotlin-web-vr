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
                translate(Vector3(0.0, 0.0, -1.0))
            }

            if (button == InputButton.DOWN) {
                translate(Vector3(0.0, 0.0, 1.0))
            }

            if (button == InputButton.LEFT) {
                translate(Vector3(-1.0, 0.0, 0.0))
            }

            if (button == InputButton.RIGHT) {
                translate(Vector3(1.0, 0.0, 0.0))
            }
        } else {
            if (button == InputButton.UP) {
                println("up")
                translate(Vector3(0.0, 1.0, 0.0))
            }

            if (button == InputButton.DOWN) {
                println("down")
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

    private fun translate(localDirection: Vector3) {
        val direction = localDirection.clone()
        val inputDeviceOrientation = Quaternion(0.0, 0.0, 0.0, 1.0)
        inputDevice.entity.getWorldQuaternion(inputDeviceOrientation)
        direction.applyQuaternion(inputDeviceOrientation)

        for (nodeUrl in inputDevice.selectedNodeUrls) {
            val node = CLIENT!!.vrController.nodes[nodeUrl] ?: return
            val nodeType = CLIENT!!.vrController.nodeTypes[nodeUrl] ?: return
            var obj = CLIENT!!.vrController.scene.getObjectByName(node.url) ?: return
            val objectOrientation = Quaternion(0.0, 0.0, 0.0, 1.0)
            obj.getWorldQuaternion(objectOrientation)

            val xAxis = Vector3(1.0, 0.0, 0.0)
            val yAxis = Vector3(0.0, 1.0, 0.0)
            val zAxis = Vector3(0.0, 0.0, 1.0)

            xAxis.applyQuaternion(objectOrientation)
            yAxis.applyQuaternion(objectOrientation)
            zAxis.applyQuaternion(objectOrientation)

            val xDirection = xAxis.dot(direction)
            val yDirection = yAxis.dot(direction)
            val zDirection = zAxis.dot(direction)

            val finalDirection: Vector3
            if (Math.abs(xDirection) >= Math.abs(yDirection) && Math.abs(xDirection) >= Math.abs(zDirection)) {
                finalDirection = xAxis.multiplyScalar(sign(xDirection))
            } else if (Math.abs(yDirection) >= Math.abs(zDirection)) {
                finalDirection = yAxis.multiplyScalar(sign(yDirection))
            } else {
                finalDirection = zAxis.multiplyScalar(sign(zDirection))
            }

            val x = node.position.x
            val y = node.position.y
            val z = node.position.z

            node.position.x += finalDirection.x * node.scale.x
            node.position.y += finalDirection.y * node.scale.y
            node.position.z += finalDirection.z * node.scale.z

            CLIENT!!.vrController.networkClient!!.send(node, nodeType)

            node.position.x = x
            node.position.y = y
            node.position.z = z
        }

    }

    fun sign(value: Double) : Double {
        return value / Math.abs(value)
    }
}