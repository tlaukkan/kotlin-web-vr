package vr.webvr.tools

import vr.CLIENT
import lib.threejs.*
import lib.threejs.Extra.BoxGeometry
import lib.threejs.Extra.SphereGeometry
import vr.network.model.PrimitiveNode
import vr.util.dynamicCast
import vr.webvr.devices.InputButton
import vr.webvr.devices.InputDevice

/**
 * Created by tlaukkan on 11/1/2016.
 */
class TravelTool(inputDevice: InputDevice) : Tool("Travel", inputDevice) {

    var lastSqueezeMoveTime: Double = 0.0
    val pointerObject: Object3D

    init {
        var geometry = SphereGeometry(0.02, 50, 50, 0.0, Math.PI * 2, 0.0, Math.PI * 2)

        val material = MeshBasicMaterial()
        material.transparent = true
        material.color = Color(0x00ffff)
        material.opacity = 0.1

        pointerObject = Mesh(geometry, material)
        pointerObject.castShadow = false
        pointerObject.receiveShadow = false
    }

    override fun active() {
        inputDevice.showSelectLine(0x00ffff)
    }

    override fun render() {
        if (inputDevice.pressedButtons.contains(InputButton.TRIGGER)) {
            var selectDistance = inputDevice.rayNodes()
            if (selectDistance != null) {
                var position = Vector3()
                inputDevice.entity.getWorldPosition(position!!)
                var orientation = Quaternion(0.0, 0.0, 0.0, 1.0)
                inputDevice.entity.getWorldQuaternion(orientation)

                val direction = Vector3(0.0, 0.0, -selectDistance!!)
                direction.applyQuaternion(orientation!!)
                position!!.add(direction)

                pointerObject.position.x = position!!.x
                pointerObject.position.y = position!!.y
                pointerObject.position.z = position!!.z
            }
        }
    }

    override fun deactivate() {
        inputDevice.hideSelectLine()
    }

    override fun onPressed(button: InputButton) {
        if (button == InputButton.TRIGGER) {
            CLIENT!!.vrController.scene.add(pointerObject)
        }
    }

    override fun onSqueezed(button: InputButton, value: Double) {
    }

    override fun onReleased(button: InputButton) {
        CLIENT!!.vrController.scene.remove(pointerObject)
        var position = Vector3()
        pointerObject.getWorldPosition(position!!)

        /*val roomPosition = virtualRealityController!!.roomGroup.position
        position.x += roomPosition.x
        position.y += roomPosition.y
        position.z += roomPosition.z*/

        CLIENT!!.vrController.roomGroup.position.x -= position.x
        CLIENT!!.vrController.roomGroup.position.y -= position.y
        CLIENT!!.vrController.roomGroup.position.z -= position.z
    }

    override fun onPadTouched(x: Double, y: Double) {
    }


}