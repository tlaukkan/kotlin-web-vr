package vr.webvr

import lib.threejs.Object3D
import vr.webvr.model.InputDevice
import lib.webvrapi.Gamepad
import lib.webvrapi.getGamepads
import lib.webvrapi.navigator
import kotlin.browser.window

class InputDeviceController(displayController: DisplayController) {

    val displayController = displayController
    var inputDevices: MutableMap<Int, InputDevice> = mutableMapOf()
    var inputDeviceModels: MutableMap<String, Object3D> = mutableMapOf()
    var inputDeviceHandlers: MutableMap<String, (controller: InputDevice) -> Unit> = mutableMapOf()

    init {
        this.update()
    }

    fun update() {
        window.setTimeout({update()}, 1000, null)

        var gamepads: Array<Gamepad> = navigator.getGamepads()
        for (gamepad: Gamepad in gamepads) {
            if (gamepad == undefined) {
                continue
            }
            //println("Got gamepad: " + gamepad.id)
            //println("Got connected: " + gamepad.connected)
            //println("Got pose: " + gamepad.pose);
            if (gamepad != null && gamepad.connected && gamepad.pose != null && this.inputDeviceHandlers[gamepad.id] != null && this.inputDevices[gamepad.index] == null) {
                var controller: InputDevice = InputDevice(gamepad.index, gamepad.id, this.inputDeviceHandlers[gamepad.id]!!)
                controller.standingMatrix = displayController.standingMatrix

                this.inputDevices[gamepad.index] = controller
                console.log("InputDevice added: " + gamepad.index + ":" + gamepad.id)
                console.log("Buttons: " + gamepad.buttons.size)
                console.log("Axes: " + gamepad.axes.size)
            }
        }

        for (index in inputDevices.keys) {
            var controller = inputDevices[index]!!
            var gamepad: Gamepad = navigator.getGamepads()[controller.index]

            // Delete controller if gamepad does not exist or is of different type.
            if (gamepad == null || (gamepad.id != controller.type || !gamepad.connected || gamepad.pose == null) ) {

                displayController.scene.remove(controller)
                inputDevices.remove(index)
                println("InputDevice removed: " + gamepad.index + ":" + gamepad.id)
                continue
            }

            // If model has not been set then attempt to set it.
            if (controller.children.size == 0) {
                // Detect gamepad type and apply appropriate model.
                if (inputDeviceModels[gamepad.id] != null) {
                    controller.add(this.inputDeviceModels[gamepad.id]!!.clone(true))

                    displayController.scene.add(controller)
                    console.log("InputDevice model set and added to scene: " + gamepad.index + ":" + gamepad.id)
                }
            }
        }

    }

}