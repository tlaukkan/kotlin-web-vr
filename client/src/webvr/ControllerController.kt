package webvr

import threejs.Object3D
import webvr.model.Controller
import webvrapi.Gamepad
import webvrapi.getGamepads
import webvrapi.navigator
import kotlin.browser.window

class ControllerController(displayController: DisplayController) {

    val displayController = displayController
    var controllers: MutableMap<Int, Controller> = mutableMapOf()
    var controllerModels: MutableMap<String, Object3D> = mutableMapOf()
    var controllerHandlers: MutableMap<String, (controller: Controller) -> Unit> = mutableMapOf()

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
            if (gamepad != null && gamepad.connected && gamepad.pose != null && this.controllerHandlers[gamepad.id] != null && this.controllers[gamepad.index] == null) {
                var controller: Controller = Controller(gamepad.index, gamepad.id, this.controllerHandlers[gamepad.id]!!)
                controller.standingMatrix = displayController.standingMatrix

                this.controllers[gamepad.index] = controller
                console.log("Controller added: " + gamepad.index + ":" + gamepad.id)
                console.log("Buttons: " + gamepad.buttons.size)
                console.log("Axes: " + gamepad.axes.size)
            }
        }

        for (index in controllers.keys) {
            var controller = controllers[index]!!
            var gamepad: Gamepad = navigator.getGamepads()[controller.index]

            // Delete controller if gamepad does not exist or is of different type.
            if (gamepad == null || (gamepad.id != controller.type || !gamepad.connected || gamepad.pose == null) ) {

                displayController.scene.remove(controller)
                controllers.remove(index)
                println("Controller removed: " + gamepad.index + ":" + gamepad.id)
                continue
            }

            // If model has not been set then attempt to set it.
            if (controller.children.size == 0) {
                // Detect gamepad type and apply appropriate model.
                if (controllerModels[gamepad.id] != null) {
                    controller.add(this.controllerModels[gamepad.id]!!.clone(true))

                    displayController.scene.add(controller)
                    console.log("Controller model set and added to scene: " + gamepad.index + ":" + gamepad.id)
                }
            }
        }

    }

}