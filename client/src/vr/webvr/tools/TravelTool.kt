package vr.webvr.tools

import vr.CLIENT
import lib.threejs.*
import lib.threejs.Extra.BoxGeometry
import lib.threejs.Extra.SphereGeometry
import vr.network.model.PrimitiveNode
import vr.util.dynamicCast
import vr.webvr.VrController
import vr.webvr.devices.InputButton
import vr.webvr.devices.InputDevice

/**
 * Created by tlaukkan on 11/1/2016.
 */
class TravelTool(inputDevice: InputDevice) : Tool("Travel", inputDevice) {

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
        gripped = false
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
        gripped = false
        inputDevice.hideSelectLine()
    }

    override fun onPressed(button: InputButton) {
        if (button == InputButton.GRIP) {
            gripped = true
        }
        if (button == InputButton.TRIGGER) {
            CLIENT!!.vrController.scene.add(pointerObject)
        }
    }

    override fun onSqueezed(button: InputButton, value: Double) {
    }

    override fun onReleased(button: InputButton) {
        if (button == InputButton.GRIP) {
            gripped = false
        }
        if (gripped) {
            if (button == InputButton.UP) {
                activeToolOnOtherDevice(HandTool::class.js.name)
            }
            if (button == InputButton.DOWN) {
                activeToolOnOtherDevice(TravelTool::class.js.name)
            }
            if (button == InputButton.LEFT) {

            }
            if (button == InputButton.RIGHT) {

            }
        } else {
            if (button == InputButton.UP) {
                activeToolOnOtherDevice(AddTool::class.js.name)
            }
            if (button == InputButton.DOWN) {
                activeToolOnOtherDevice(RemoveTool::class.js.name)
            }
            if (button == InputButton.LEFT) {
                activeToolOnOtherDevice(BuildTool::class.js.name)
            }
            if (button == InputButton.RIGHT) {
                activeToolOnOtherDevice(RotateTool::class.js.name)
            }

            if (button == InputButton.TRIGGER) {
                CLIENT!!.vrController.scene.remove(pointerObject)

                //var inputDevicePosition = Vector3()
                //inputDevice.entity.getWorldPosition(inputDevicePosition!!)

                var inputDevicePosition = Vector3()
                CLIENT!!.displayController.camera.getWorldPosition(inputDevicePosition!!)

                println(inputDevicePosition.x)
                println(inputDevicePosition.y)
                println(inputDevicePosition.z)

                var pointerPosition = Vector3()
                pointerObject.getWorldPosition(pointerPosition!!)

                // Room floor should be at pointer y position
                inputDevicePosition.y = 0.0

                var travelTranslation = pointerPosition.clone()
                travelTranslation.sub(inputDevicePosition)

                CLIENT!!.vrController.roomGroup.position.x -= travelTranslation.x
                CLIENT!!.vrController.roomGroup.position.y -= travelTranslation.y
                CLIENT!!.vrController.roomGroup.position.z -= travelTranslation.z
            }
        }

    }

    override fun onPadTouched(x: Double, y: Double) {
    }

    fun activeToolOnOtherDevice(toolClassName: String) {
        val otherInputDevice = getOtherInputDevice() ?: return
        otherInputDevice.activateToolByClass(toolClassName)
    }

    fun getOtherInputDevice() : InputDevice? {
        println("Finding other input device.")
        for (inputDeviceCandidate in CLIENT!!.inputController.inputDevices.values) {
            if (inputDeviceCandidate != inputDevice) {
                println("Found other input device (self is ${inputDevice.index}) : ${inputDeviceCandidate.index} ${inputDeviceCandidate.type}")
                return inputDeviceCandidate
            }
        }
        println("Did not find other input device.")
        return null
    }

}