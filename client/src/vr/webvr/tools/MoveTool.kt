package vr.webvr.tools

import vr.CLIENT
import lib.threejs.*
import vr.network.model.Node
import vr.network.model.PrimitiveNode
import vr.util.dynamicCast
import vr.util.getDeltaQuaternion
import vr.webvr.devices.InputButton
import vr.webvr.devices.InputDevice

/**
 * Created by tlaukkan on 11/1/2016.
 */
class MoveTool(inputDevice: InputDevice) : Tool("Move", inputDevice) {

    var selectRayDistance: Double = 0.0

    var selectPointStartPosition: Vector3 = Vector3()
    var inputDeviceStartOrientation: Quaternion = Quaternion(0.0, 0.0, 0.0, 1.0)

    var objectStartPosition: Vector3 = Vector3()
    var objectStartOrientation: Quaternion = Quaternion(0.0, 0.0, 0.0, 1.0)

    var actualObject: Object3D? = null
    var protoObject: Object3D? = null

    var lastSqueezeMovedNodeTime: Double = 0.0

    override fun active() {
        inputDevice.showSelectLine(0x0000ff)
    }

    override fun render() {
        if (inputDevice.pressedButtons.contains(InputButton.TRIGGER)) {
            moveNodeTo(false)
        }
    }

    override fun deactivate() {
        inputDevice.hideSelectLine()
    }

    override fun onPressed(button: InputButton) {
        if (button == InputButton.TRIGGER) {
            inputDevice.unselectNodes()
            selectRayDistance = inputDevice.selectNodes() ?: return

            val nodeUrl = inputDevice.selectedNodeUrls[0]

            inputDeviceStartOrientation = Quaternion(0.0, 0.0, 0.0, 1.0)
            inputDevice.entity.getWorldQuaternion(inputDeviceStartOrientation)

            selectPointStartPosition = Vector3()
            inputDevice.entity.getWorldPosition(selectPointStartPosition)
            // Move position to intersection point of select ray
            val direction = Vector3(0.0, 0.0, -selectRayDistance)
            direction.applyQuaternion(inputDeviceStartOrientation)
            selectPointStartPosition.add(direction)

            inputDevice.entity.getWorldQuaternion(inputDeviceStartOrientation)

            val obj = CLIENT!!.vrController.scene.getObjectByName(nodeUrl) ?: return

            objectStartPosition = Vector3()
            obj.getWorldPosition(objectStartPosition)
            objectStartOrientation = Quaternion(0.0, 0.0, 0.0, 1.0)
            obj.getWorldQuaternion(objectStartOrientation)
        }
    }

    override fun onSqueezed(button: InputButton, value: Double) {
        if (button == InputButton.TRIGGER) {
            if (inputDevice.selectedNodeUrls.size > 0) {

                // Sends node move information to server.
                if (CLIENT!!.renderTime - lastSqueezeMovedNodeTime > 0.15) {
                    moveNodeTo(true)
                    lastSqueezeMovedNodeTime = CLIENT!!.renderTime
                }
            }
        }
    }

    override fun onReleased(button: InputButton) {
        if (button == InputButton.TRIGGER) {
            if (inputDevice.selectedNodeUrls.size > 0) {
                moveNodeTo(true)
                inputDevice.unselectNodes()

                if (protoObject != null) {
                    CLIENT!!.vrController.roomGroup.remove(protoObject!!)
                    actualObject!!.visible = true
                    actualObject = null
                    protoObject = null
                }

            }
        }
    }

    override fun onPadTouched(x: Double, y: Double) {
    }

    private fun moveNodeTo(sendNodeUpdate: Boolean) {
        if (inputDevice.selectedNodeUrls == null || inputDevice.selectedNodeUrls.size == 0) {
            return
        }
        val nodeUrl = inputDevice.selectedNodeUrls[0]

        val inputDeviceCurrentOrientation = Quaternion(0.0, 0.0, 0.0, 1.0)
        inputDevice.entity.getWorldQuaternion(inputDeviceCurrentOrientation)

        val selectPointCurrentPosition = Vector3()
        inputDevice.entity.getWorldPosition(selectPointCurrentPosition)
        // Move position to intersection point of select ray
        val direction = Vector3(0.0, 0.0, -selectRayDistance)
        direction.applyQuaternion(inputDeviceCurrentOrientation)
        selectPointCurrentPosition.add(direction)

        val selectPointPositionChange = selectPointCurrentPosition.clone()
        selectPointPositionChange.sub(selectPointStartPosition)

        val updatedObjectPosition = objectStartPosition.clone()
        updatedObjectPosition.add(selectPointPositionChange)

        val updatedObjectOrientation = getDeltaQuaternion(inputDeviceStartOrientation, inputDeviceCurrentOrientation)
        updatedObjectOrientation.multiply(objectStartOrientation)

        val node = CLIENT!!.vrController.nodes[nodeUrl] ?: return
        val nodeType = CLIENT!!.vrController.nodeTypes[nodeUrl] ?: return

        CLIENT!!.vrController.setNodePositionFromWorldCoordinates(node, updatedObjectPosition)

        node.orientation.x = updatedObjectOrientation.x
        node.orientation.y = updatedObjectOrientation.y
        node.orientation.z = updatedObjectOrientation.z
        node.orientation.w = updatedObjectOrientation.w

        if (sendNodeUpdate) {
            CLIENT!!.vrController.networkClient!!.send(node, nodeType)
        }

        if (protoObject == null) {
            var obj = CLIENT!!.vrController.scene.getObjectByName(node.url)
            if (obj != null) {
                actualObject = obj
                protoObject = obj.clone(true)
                actualObject!!.visible = false
                protoObject!!.name = "proto"
                CLIENT!!.vrController.roomGroup.add(protoObject!!)
            }
        }

        if (protoObject != null) {
            protoObject!!.position.x = node.position.x
            protoObject!!.position.y = node.position.y
            protoObject!!.position.z = node.position.z

            protoObject!!.quaternion.x = node.orientation.x
            protoObject!!.quaternion.y = node.orientation.y
            protoObject!!.quaternion.z = node.orientation.z
            protoObject!!.quaternion.w = node.orientation.w
        }
    }

}