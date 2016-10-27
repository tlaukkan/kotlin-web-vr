package webvr

import webvr.model.InputDevice

/**
 * Created by tlaukkan on 10/27/2016.
 */
class InputController(inputDeviceController: InputDeviceController) {

    init {

        inputDeviceController.inputDeviceHandlers["OpenVR Gamepad"] = fun (controller: InputDevice) {
            var gamepad = controller.gamepad!!
            var padTouched: Boolean = false
            for (button in gamepad.buttons) {
                var i = gamepad.buttons.indexOf(button)
                if (button.pressed) {
                    console.log("Button $i pressed with value: ${button.value}")
                }
                if (button.touched) {
                    console.log("Button $i touched with value: ${button.value}")
                }
                if (i == 0 && button.touched) {
                    padTouched = true
                }
            }

            var axes = gamepad.axes
            for (axis in axes) {
                var i = axes.indexOf(axis)
                if (padTouched) {
                    console.log("Axis $i: $axis")
                }
            }
        }

    }


}