package vr.webvr

import vr.webvr.model.InputDevice

class OpenVrGamepad(inputDeviceController: InputDeviceController) {

    var pressed: MutableList<InputButton> = mutableListOf()

    var onSqueezed : ((button: InputButton, value: Double) -> Unit)? = null
    var onPressed: ((button: InputButton) -> Unit)? = null
    var onReleased: ((button: InputButton) -> Unit)? = null
    var onPadTouched: ((x: Double, y: Double) -> Unit)? = null

    init {

        inputDeviceController.inputDeviceHandlers["OpenVR Gamepad"] = fun (controller: InputDevice) {

            var gamepad = controller.gamepad!!
            var axes = gamepad.axes
            var x = axes[0].toDouble()
            var y = axes[1].toDouble()

            for (button in gamepad.buttons) {
                var i = gamepad.buttons.indexOf(button)

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
                    if (!pressed.contains(inputButton)) {
                        pressed.add(inputButton)
                        pressed(inputButton)
                    }
                } else {
                    if (pressed.contains(inputButton)) {
                        pressed.remove(inputButton)
                        released(inputButton)
                    }
                }

                if (button.value.toDouble() > 0) {
                    squeezed(inputButton, button.value.toDouble())
                }
            }

        }

    }

    fun squeezed(button: InputButton, value: Double) {
        if (onSqueezed != null) {
            onSqueezed!!(button, value)
        }
    }

    fun pressed(button: InputButton) {
        if (onPressed != null) {
            onPressed!!(button)
        }
    }

    fun released(button: InputButton) {
        if (onReleased != null) {
            onReleased!!(button)
        }
    }

    fun touchPadTouched(x: Double, y: Double) {
        if (onPadTouched != null) {
            onPadTouched!!(x,y)
        }
    }


}