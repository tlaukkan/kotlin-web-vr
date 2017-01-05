package vr.webvr.tools

import lib.threejs.*
import lib.threejs.Extra.BoxGeometry
import lib.threejs.Extra.SphereGeometry
import renderTime
import virtualRealityController
import vr.network.model.PrimitiveNode
import vr.util.dynamicCast
import vr.webvr.devices.InputButton
import vr.webvr.devices.InputDevice

/**
 * Created by tlaukkan on 11/1/2016.
 */
class TravelTool(inputDevice: InputDevice) : Tool("Travel tool", inputDevice) {

    var lastSqueezeMoveTime: Double = 0.0
    val pointerObject: Object3D

    init {
        var geometry = SphereGeometry(0.02, 50, 50, 0.0, Math.PI * 2, 0.0, Math.PI * 2)

        val material = MeshBasicMaterial()
        material.transparent = true
        material.color = Color(0x00ffff)
        material.opacity = 0.05

        pointerObject = Mesh(geometry, material)
        pointerObject.castShadow = false
        pointerObject.receiveShadow = false
    }

    override fun active() {
        inputDevice.showSelectLine(0x00ffff)
    }

    override fun deactive() {
        inputDevice.hideSelectLine()
    }

    override fun onPressed(button: InputButton) {
        if (button == InputButton.TRIGGER) {
            var selectDistance = inputDevice.rayNodes()
            if (selectDistance != null) {
                var startPosition = Vector3()
                inputDevice.entity.getWorldPosition(startPosition!!)
                var startOrientation = Quaternion(0.0, 0.0, 0.0, 1.0)
                inputDevice.entity.getWorldQuaternion(startOrientation)

                val direction = Vector3(0.0, 0.0, -selectDistance!!)
                direction.applyQuaternion(startOrientation!!)
                startPosition!!.add(direction)

                pointerObject.position.x = startPosition!!.x
                pointerObject.position.y = startPosition!!.y
                pointerObject.position.z = startPosition!!.z

                virtualRealityController!!.scene.add(pointerObject)
            }
        }
    }

    override fun onSqueezed(button: InputButton, value: Double) {
        if (button == InputButton.TRIGGER) {
            var selectDistance = inputDevice.rayNodes()
            if (selectDistance != null) {
                var startPosition = Vector3()
                inputDevice.entity.getWorldPosition(startPosition!!)
                var startOrientation = Quaternion(0.0, 0.0, 0.0, 1.0)
                inputDevice.entity.getWorldQuaternion(startOrientation)

                val direction = Vector3(0.0, 0.0, -selectDistance!!)
                direction.applyQuaternion(startOrientation!!)
                startPosition!!.add(direction)

                pointerObject.position.x = startPosition!!.x
                pointerObject.position.y = startPosition!!.y
                pointerObject.position.z = startPosition!!.z
                if (renderTime - lastSqueezeMoveTime > 0.15) {
                    lastSqueezeMoveTime = renderTime
                }
            }
        }
    }

    override fun onReleased(button: InputButton) {
        if (button == InputButton.TRIGGER) {
            virtualRealityController!!.scene.remove(pointerObject)
        }
    }

    override fun onPadTouched(x: Double, y: Double) {
    }


}