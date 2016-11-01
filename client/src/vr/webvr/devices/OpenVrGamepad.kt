package vr.webvr.devices

import vr.webvr.InputDeviceController

class OpenVrGamepad(inputDeviceController: InputDeviceController, index: Int, type: String) : InputDevice(index, type) {

    override fun processInput() {

        var axes = gamepad!!.axes
        var x = axes[0].toDouble()
        var y = axes[1].toDouble()

        for (button in gamepad!!.buttons) {
            var i = gamepad!!.buttons.indexOf(button)

            val inputButton : InputButton
            if (i == 0) {

                if (button.touched) {
                    touchPadTouched(x, y)
                }

                if (x < 0) {
                    if (y > 0) {
                        if (-x > y) {
                            inputButton = InputButton.LEFT
                        } else {
                            inputButton = InputButton.UP
                        }
                    } else {
                        if (-x > -y) {
                            inputButton = InputButton.LEFT
                        } else {
                            inputButton = InputButton.DOWN
                        }
                    }
                } else {
                    if (y > 0) {
                        if (x > y) {
                            inputButton = InputButton.RIGHT
                        } else {
                            inputButton = InputButton.UP
                        }
                    } else {
                        if (x > -y) {
                            inputButton = InputButton.RIGHT
                        } else {
                            inputButton = InputButton.DOWN
                        }
                    }
                }

            } else if (i == 1) {
                inputButton = InputButton.TRIGGER
            } else if (i == 2) {
                inputButton = InputButton.GRIP
            } else if (i == 3) {
                inputButton = InputButton.MENU
            } else {
                break
            }

            if (button.pressed) {
                if (!pressedButtons.contains(inputButton)) {
                    pressedButtons.add(inputButton)
                    pressed(inputButton)
                }
            } else {
                if (pressedButtons.contains(inputButton)) {
                    pressedButtons.remove(inputButton)
                    released(inputButton)
                }
            }

            if (button.value.toDouble() > 0) {
                squeezed(inputButton, button.value.toDouble())
            }
        }

    }



}