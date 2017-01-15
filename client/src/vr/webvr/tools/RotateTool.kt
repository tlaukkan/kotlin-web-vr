package vr.webvr.tools

import lib.threejs.Quaternion
import lib.threejs.Vector3
import vr.CLIENT
import vr.network.model.Node
import vr.util.dynamicCast
import vr.util.fromJson
import vr.util.getDeltaQuaternion
import vr.util.toJson
import vr.webvr.devices.InputButton
import vr.webvr.devices.InputDevice



/**
 * Created by tlaukkan on 11/1/2016.
 */
class RotateTool(inputDevice: InputDevice) : Tool("Rotate", inputDevice) {

    override fun active() {
        gripped = false
        inputDevice.showSelectLine(0x800080)
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
        if (button == InputButton.TRIGGER) {
            if (inputDevice.selectNodes() == null) {
                inputDevice.unselectNodes()
            }
        }
    }

    override fun onReleased(button: InputButton) {
        if (button == InputButton.GRIP) {
            gripped = false
        }

        if (!gripped) {
            if (button == InputButton.UP) {
                rotate(Vector3(-1.0, 0.0, 0.0))
            }

            if (button == InputButton.DOWN) {
                rotate(Vector3(1.0, 0.0, 0.0))
            }

            if (button == InputButton.LEFT) {
                rotate(Vector3(0.0, -1.0, 0.0))
            }

            if (button == InputButton.RIGHT) {
                rotate(Vector3(0.0, 1.0, 0.0))
            }
        } else {
            if (button == InputButton.LEFT) {
                println("up")
                rotate(Vector3(0.0, 0.0, 1.0))
            }

            if (button == InputButton.RIGHT) {
                println("down")
                rotate(Vector3(0.0, 0.0,-1.0))
            }
            if (button == InputButton.TRIGGER) {
                for (nodeUrl in inputDevice.selectedNodeUrls) {
                    val node = CLIENT!!.vrController.nodes[nodeUrl] ?: return
                    val nodeType = CLIENT!!.vrController.nodeTypes[nodeUrl] ?: return
                    node.orientation.x = 0.0
                    node.orientation.y = 0.0
                    node.orientation.z = 0.0
                    node.orientation.w = 1.0
                    CLIENT!!.vrController.networkClient!!.send(node, nodeType)
                }
            }
        }
    }

    override fun onSqueezed(button: InputButton, value: Double) {
    }

    override fun onPadTouched(x: Double, y: Double) {
    }

    private fun rotate(localRotationAxis: Vector3) {
        val finalRotationAxis = localRotationAxis.clone()
        val inputDeviceOrientation = Quaternion(0.0, 0.0, 0.0, 1.0)
        inputDevice.entity.getWorldQuaternion(inputDeviceOrientation)
        finalRotationAxis.applyQuaternion(inputDeviceOrientation)

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

            val xDirection = xAxis.dot(finalRotationAxis)
            val yDirection = yAxis.dot(finalRotationAxis)
            val zDirection = zAxis.dot(finalRotationAxis)

            val finalDirection: Vector3
            if (Math.abs(xDirection) >= Math.abs(yDirection) && Math.abs(xDirection) >= Math.abs(zDirection)) {
                finalDirection = xAxis.multiplyScalar(sign(xDirection))
            } else if (Math.abs(yDirection) >= Math.abs(zDirection)) {
                finalDirection = yAxis.multiplyScalar(sign(yDirection))
            } else {
                finalDirection = zAxis.multiplyScalar(sign(zDirection))
            }

            val rotation = Quaternion(0.0, 0.0, 0.0, 1.0)
            rotation.setFromAxisAngle(finalDirection, 2.0 * Math.PI / 16)

            val nodeOrientation = Quaternion(0.0, 0.0, 0.0, 1.0)
            nodeOrientation.x = node.orientation.x
            nodeOrientation.y = node.orientation.y
            nodeOrientation.z = node.orientation.z
            nodeOrientation.w = node.orientation.w
            rotation.multiply(nodeOrientation)

            val x = node.orientation.x
            val y = node.orientation.y
            val z = node.orientation.z
            val w = node.orientation.w

            node.orientation.x = rotation.x
            node.orientation.y = rotation.y
            node.orientation.z = rotation.z
            node.orientation.w = rotation.w

            CLIENT!!.vrController.networkClient!!.send(node, nodeType)

            node.orientation.x = x
            node.orientation.y = y
            node.orientation.z = z
            node.orientation.w = w
        }

    }

    fun sign(value: Double) : Double {
        return value / Math.abs(value)
    }
}