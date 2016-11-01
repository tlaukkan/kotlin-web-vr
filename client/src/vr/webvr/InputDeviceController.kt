package vr.webvr

import lib.threejs.Object3D
import vr.webvr.devices.InputDevice
import lib.webvrapi.Gamepad
import lib.webvrapi.getGamepads
import lib.webvrapi.navigator
import vr.webvr.devices.OpenVrGamepad
import vr.webvr.tools.DefaultTool
import kotlin.browser.window

class InputDeviceController(displayController: DisplayController) {

    val displayController = displayController
    var inputDevices: MutableMap<Int, InputDevice> = mutableMapOf()
    var inputDeviceModels: MutableMap<String, Object3D> = mutableMapOf()

    init {
        this.detectInputDevices()
        this.processInput()
    }

    fun render() {
        for (inputDevice in inputDevices.values) {
            if (inputDevice.gamepad != null) {
                inputDevice.render()
            }
        }
    }

    fun processInput() {
        window.setTimeout({ processInput() }, 100, null)

        for (inputDevice in inputDevices.values) {
            if (inputDevice.gamepad != null) {
                inputDevice.processInput()
            }
        }
    }

    fun detectInputDevices() {
        window.setTimeout({ detectInputDevices()}, 1000, null)

        var gamepads: Array<Gamepad> = navigator.getGamepads()
        for (gamepad: Gamepad in gamepads) {
            if (gamepad == undefined) {
                continue
            }

            if (gamepad != null && gamepad.connected && gamepad.pose != null && this.inputDevices[gamepad.index] == null) {
                val controller: InputDevice
                if ("OpenVR Gamepad".equals(gamepad.id)) {
                    controller = OpenVrGamepad(this, gamepad.index, gamepad.id)
                } else {
                    println("Unknown gamepad ${gamepad.id}")
                    continue
                }

                controller.tools.add(DefaultTool(controller))
                controller.tools[0].activate()

                controller.standingMatrix = displayController.standingMatrix

                this.inputDevices[gamepad.index] = controller
                println("InputDevice added: " + gamepad.index + ":" + gamepad.id)
            }
        }

        for (index in inputDevices.keys) {
            var controller = inputDevices[index]!!
            var gamepad: Gamepad = navigator.getGamepads()[controller.index]

            // Delete controller if gamepad does not exist or is of different type.
            if (gamepad == null || (gamepad.id != controller.type || !gamepad.connected || gamepad.pose == null) ) {

                displayController.scene.remove(controller.entity)
                inputDevices.remove(index)
                println("InputDevice removed: " + gamepad.index + ":" + gamepad.id)
                continue
            }

            // If model has not been set then attempt to set it.
            if (controller.entity.children.size == 0) {
                // Detect gamepad type and apply appropriate model.
                if (inputDeviceModels[gamepad.id] != null) {
                    controller.entity.add(this.inputDeviceModels[gamepad.id]!!.clone(true))

                    displayController.scene.add(controller.entity)
                }
            }
        }

    }

}