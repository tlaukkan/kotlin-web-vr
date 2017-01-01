package vr.webvr.tools

import lib.threejs.*
import renderTime
import virtualRealityController
import vr.network.model.PrimitiveNode
import vr.util.dynamicCast
import vr.webvr.devices.InputButton
import vr.webvr.devices.InputDevice

/**
 * Created by tlaukkan on 11/1/2016.
 */
class MoveTool(inputDevice: InputDevice) : Tool("Move tool", inputDevice) {

    var selectDistance: Double? = null

    var startPosition: Vector3? = null
    var startOrientation: Quaternion? = null

    var objectStartPosition: Vector3? = null
    var objectStartOrientation: Quaternion? = null

    override fun active() {
        inputDevice.showSelectLine(0x0000ff)
    }

    override fun deactive() {
        inputDevice.hideSelectLine()
    }

    override fun onPressed(button: InputButton) {
        if (button == InputButton.TRIGGER) {
            inputDevice.unselectNodes()
            selectDistance = inputDevice.selectNodes()
            if (inputDevice.selectedNodeUrls.size > 0) {
                val nodeUrl = inputDevice.selectedNodeUrls[0]

                startPosition = Vector3()
                inputDevice.entity.getWorldPosition(startPosition!!)
                startOrientation = Quaternion(0.0, 0.0, 0.0, 1.0)

                // Move position to intersection point of select rate
                /*val direction = Vector3(0.0, 0.0, -selectDistance!!)
                direction.applyQuaternion(startOrientation!!)
                startPosition!!.add(direction)*/

                inputDevice.entity.getWorldQuaternion(startOrientation!!)

                val obj = virtualRealityController!!.scene.getObjectByName(nodeUrl) ?: return

                objectStartPosition = Vector3()
                obj.getWorldPosition(objectStartPosition!!)
                objectStartOrientation = Quaternion(0.0, 0.0, 0.0, 1.0)
                obj.getWorldQuaternion(objectStartOrientation!!)

            }
        }
    }

    var lastSqueezeMoveTime: Double = 0.0

    override fun onSqueezed(button: InputButton, value: Double) {
        if (button == InputButton.TRIGGER) {
            if (inputDevice.selectedNodeUrls.size > 0) {

                if (renderTime - lastSqueezeMoveTime > 0.15) {
                    moveNodeTo()
                    lastSqueezeMoveTime = renderTime
                }
            }
        }
    }

    override fun onReleased(button: InputButton) {
        if (button == InputButton.TRIGGER) {
            if (inputDevice.selectedNodeUrls.size > 0) {
                moveNodeTo()
                inputDevice.unselectNodes()
            }
        }
    }

    override fun onPadTouched(x: Double, y: Double) {
    }

    private fun moveNodeTo() {
        val nodeUrl = inputDevice.selectedNodeUrls[0]

        val currentPosition = Vector3()
        inputDevice.entity.getWorldPosition(currentPosition!!)
        val currentOrientation = Quaternion(0.0, 0.0, 0.0, 1.0)
        inputDevice.entity.getWorldQuaternion(currentOrientation!!)

        // Move position to intersection point of select rate
        /*val direction = Vector3(0.0, 0.0, -selectDistance!!)
        direction.applyQuaternion(currentOrientation!!)
        currentPosition!!.add(direction)*/

        //val obj = virtualRealityController!!.scene.getObjectByName(nodeUrl) ?: return

        val positionChange = currentPosition!!.clone()
        positionChange.sub(startPosition!!)

        val objectPosition = objectStartPosition!!.clone()
        objectPosition.add(positionChange)

        val originalOrientation = startOrientation!!.clone()
        val originalOrientationConjugate = originalOrientation.conjugate()

        val orientationChange = currentOrientation.clone()
        orientationChange.multiply(originalOrientationConjugate)

        val objectOrientation = objectStartOrientation!!.clone()
        //inputDevice.entity.getWorldQuaternion(objectOrientation!!)
        orientationChange.multiply(objectOrientation)

        val node = virtualRealityController!!.nodes[nodeUrl] ?: return
        val nodeType = virtualRealityController!!.nodeTypes[nodeUrl]!! ?: return

        node.position.x = objectPosition.x
        node.position.y = objectPosition.y
        node.position.z = objectPosition.z

        node.orientation.x = orientationChange.x
        node.orientation.y = orientationChange.y
        node.orientation.z = orientationChange.z
        node.orientation.w = orientationChange.w

        virtualRealityController!!.networkClient!!.send(node, nodeType)
    }

}