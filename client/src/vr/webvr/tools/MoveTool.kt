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

    var selectRayDistance: Double? = null

    var inputDeviceStartPosition: Vector3? = null
    var inputDeviceStartOrientation: Quaternion? = null

    var objectStartPosition: Vector3? = null
    var objectStartOrientation: Quaternion? = null

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
            selectRayDistance = inputDevice.selectNodes()

            if (inputDevice.selectedNodeUrls.size == 0) {
                return
            }

            val nodeUrl = inputDevice.selectedNodeUrls[0]

            inputDeviceStartPosition = Vector3()
            inputDevice.entity.getWorldPosition(inputDeviceStartPosition!!)

            inputDeviceStartOrientation = Quaternion(0.0, 0.0, 0.0, 1.0)
            inputDevice.entity.getWorldQuaternion(inputDeviceStartOrientation!!)

            // Move position to intersection point of select rate
            val direction = Vector3(0.0, 0.0, -selectRayDistance!!)
            direction.applyQuaternion(inputDeviceStartOrientation!!)
            inputDeviceStartPosition!!.add(direction)

            inputDevice.entity.getWorldQuaternion(inputDeviceStartOrientation!!)

            val obj = CLIENT!!.vrController.scene.getObjectByName(nodeUrl) ?: return

            objectStartPosition = Vector3()
            obj.getWorldPosition(objectStartPosition!!)
            objectStartOrientation = Quaternion(0.0, 0.0, 0.0, 1.0)
            obj.getWorldQuaternion(objectStartOrientation!!)
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
        val nodeUrl = inputDevice.selectedNodeUrls[0]

        val currentPosition = Vector3()
        inputDevice.entity.getWorldPosition(currentPosition!!)
        val currentOrientation = Quaternion(0.0, 0.0, 0.0, 1.0)
        inputDevice.entity.getWorldQuaternion(currentOrientation!!)

        // Move position to intersection point of select rate
        val direction = Vector3(0.0, 0.0, -selectRayDistance!!)
        direction.applyQuaternion(currentOrientation!!)
        currentPosition!!.add(direction)

        //val obj = virtualRealityController!!.scene.getObjectByName(nodeUrl) ?: return

        val positionChange = currentPosition!!.clone()
        positionChange.sub(inputDeviceStartPosition!!)

        val objectPosition = objectStartPosition!!.clone()
        objectPosition.add(positionChange)

        val originalOrientation = inputDeviceStartOrientation!!.clone()
        val orientationChange = getDeltaQuaternion(originalOrientation, currentOrientation)

        val objectOrientation = objectStartOrientation!!.clone()
        orientationChange.multiply(objectOrientation)

        val node = CLIENT!!.vrController.nodes[nodeUrl] ?: return
        val nodeType = CLIENT!!.vrController.nodeTypes[nodeUrl]!! ?: return

        CLIENT!!.vrController.setNodePosition(node!!, objectPosition)

        node.orientation.x = orientationChange.x
        node.orientation.y = orientationChange.y
        node.orientation.z = orientationChange.z
        node.orientation.w = orientationChange.w

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