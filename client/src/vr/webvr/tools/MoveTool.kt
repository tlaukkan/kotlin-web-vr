package vr.webvr.tools

import lib.threejs.*
import virtualRealityController
import vr.network.model.PrimitiveNode
import vr.util.dynamicCast
import vr.webvr.devices.InputButton
import vr.webvr.devices.InputDevice

/**
 * Created by tlaukkan on 11/1/2016.
 */
class MoveTool(inputDevice: InputDevice) : Tool("Move tool", inputDevice) {

    var startPosition: Vector3? = null
    var startOrientation: Quaternion? = null

    override fun active() {
        inputDevice.showSelectLine()
    }

    override fun deactive() {
        inputDevice.hideSelectLine()
    }

    override fun onPressed(button: InputButton) {
        if (button == InputButton.TRIGGER) {
            inputDevice.unselectNodes()
            inputDevice.selectNodes()
        }
        if (inputDevice.selectedNodeUrls.size > 0) {
            val nodeUrl = inputDevice.selectedNodeUrls[0]

            startPosition = Vector3()
            inputDevice.entity.getWorldPosition(startPosition!!)
            startOrientation = Quaternion(0.0, 0.0, 0.0, 1.0)
            inputDevice.entity.getWorldQuaternion(startOrientation!!)
        }
    }

    override fun onReleased(button: InputButton) {
        if (inputDevice.selectedNodeUrls.size > 0) {
            val nodeUrl = inputDevice.selectedNodeUrls[0]

            val currentPosition = Vector3()
            inputDevice.entity.getWorldPosition(currentPosition!!)
            val currentOrientation = Quaternion(0.0, 0.0, 0.0, 1.0)
            inputDevice.entity.getWorldQuaternion(currentOrientation!!)

            val positionChange = currentPosition!!.clone()
            positionChange.sub(startPosition!!)

            val originalOrientation = startOrientation!!.clone()
            val originalOrientationConjugate = originalOrientation.conjugate()
            val orientationChange = currentOrientation.clone()
            orientationChange.multiply(originalOrientationConjugate)

            val obj = virtualRealityController!!.scene.getObjectByName(nodeUrl) ?: return

            val objectPosition  = Vector3()
            obj.getWorldPosition(objectPosition!!)
            val objectOrientation = Quaternion(0.0, 0.0, 0.0, 1.0)
            inputDevice.entity.getWorldQuaternion(objectOrientation!!)

            objectPosition.add(positionChange)
            objectOrientation.multiply(orientationChange)

            val node : PrimitiveNode = dynamicCast(virtualRealityController!!.nodes[nodeUrl] ?: return)
            node.position.x = objectPosition.x
            node.position.y = objectPosition.y
            node.position.z = objectPosition.z

            node.orientation.x = objectOrientation.x
            node.orientation.y = objectOrientation.y
            node.orientation.z = objectOrientation.z
            node.orientation.w = objectOrientation.w

            virtualRealityController!!.networkClient!!.send(listOf(node))
        }

    }

    override fun onSqueezed(button: InputButton, value: Double) {
    }

    override fun onPadTouched(x: Double, y: Double) {
    }

}