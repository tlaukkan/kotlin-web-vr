package vr.webvr.tools

import lib.threejs.Object3D
import virtualRealityController
import vr.network.model.PrimitiveNode
import vr.webvr.devices.InputButton
import vr.webvr.devices.InputDevice

/**
 * Created by tlaukkan on 11/1/2016.
 */
class AddTool(inputDevice: InputDevice) : Tool("Add Tool", inputDevice) {

    private enum class AddMode {
        PRIMITIVE, MODEL
    }

    private var selectedMode: AddMode = AddMode.PRIMITIVE

    private var primitives = listOf("box", "sphere", "plane")

    private var selectedPrimitive = primitives[0]

    private var protoObject: Object3D? = null


    fun updateDisplay() {
        var text =
                    "$name\n" +
                    "Mode: $selectedMode\n"

        if (selectedMode == AddMode.PRIMITIVE) {
            for (primitive in primitives) {
                val index = primitives.indexOf(primitive)
                if (primitive.equals(selectedPrimitive)) {
                    text += "$index) $primitive <-\n"
                } else {
                    text += "$index) $primitive \n"
                }
            }
        }

        inputDevice.display(text)
    }

    fun updateObject() {
        if (protoObject != null) {
            inputDevice.entity.remove(protoObject!!)
        }
        if (selectedMode == AddMode.PRIMITIVE) {
            val node = PrimitiveNode("00000000-0000-0000-0000-000000000000", "box", "textures/alien.jpg")

            virtualRealityController!!.nodeActuators["PrimitiveNode"]!!.construct(node, { obj: Object3D? ->
                if (obj != null) {
                    protoObject = obj
                    obj.position.z = -0.25
                    obj.scale.x = 0.25
                    obj.scale.y = 0.25
                    obj.scale.z = 0.25
                    inputDevice.entity.add(protoObject!!)
                }
            })
        }
    }


    override fun active() {
        updateDisplay()
        updateObject()
    }

    override fun deactive() {
        if (protoObject != null) {
            inputDevice.entity.remove(protoObject!!)
        }
    }

    override fun onPressed(button: InputButton) {
        //println("Pressed: $button")
    }

    override fun onReleased(button: InputButton) {
        //println("Released: $button")
        if (button == InputButton.RIGHT) {
            var newMode = selectedMode.ordinal + 1
            if (newMode >= AddMode.values().size) {
                newMode = 0
            }
            selectedMode = AddMode.values()[newMode]
            updateDisplay()
            updateObject()
        }
        if (button == InputButton.LEFT) {
            var newMode = selectedMode.ordinal - 1
            if (newMode < 0) {
                newMode = AddMode.values().size - 1
            }
            selectedMode = AddMode.values()[newMode]
            updateDisplay()
            updateObject()
        }
        if (button == InputButton.UP) {
            var newPrimitive = primitives.indexOf(selectedPrimitive) - 1
            if (newPrimitive < 0) {
                newPrimitive = primitives.size - 1
            }
            selectedPrimitive = primitives[newPrimitive]
            updateDisplay()
            updateObject()
        }
        if (button == InputButton.DOWN) {
            var newPrimitive = primitives.indexOf(selectedPrimitive) + 1
            if (newPrimitive >= primitives.size) {
                newPrimitive = 0
            }
            selectedPrimitive = primitives[newPrimitive]
            updateDisplay()
            updateObject()
        }
    }

    override fun onSqueezed(button: InputButton, value: Double) {
        //println("Squeezed: $button $value")
    }

    override fun onPadTouched(x: Double, y: Double) {
        //println("Pad touched: $x,$y")
    }

}