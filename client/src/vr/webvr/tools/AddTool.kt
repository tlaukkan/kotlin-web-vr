package vr.webvr.tools

import lib.threejs.Object3D
import lib.threejs.Quaternion
import lib.threejs.Vector3
import virtualRealityController
import vr.network.model.Node
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

    private var protoNode: Node? = null

    private var scale = 0.25

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
            val linkedServerUrl = virtualRealityController!!.linkedServerCellUrl
            println(linkedServerUrl)
            val nodeUrl = "${virtualRealityController!!.linkedServerCellUrl}/00000000-0000-0000-0000-000000000000"
            println(nodeUrl)
            protoNode = PrimitiveNode("box", "textures/alien.jpg")
            protoNode!!.url = nodeUrl

            virtualRealityController!!.nodeActuators["PrimitiveNode"]!!.construct(protoNode!!, { obj: Object3D? ->
                if (obj != null) {
                    protoObject = obj
                    obj.position.z = -scale
                    obj.scale.x = scale
                    obj.scale.y = scale
                    obj.scale.z = scale
                    inputDevice.entity.add(protoObject!!)
                }
            })
        }
    }

    fun addObject() {
        if (protoNode == null) {
            return
        }

        val position = Vector3()
        protoObject!!.getWorldPosition(position)
        val orientation = Quaternion(0.0, 0.0, 0.0, 1.0)
        protoObject!!.getWorldQuaternion(orientation)

        virtualRealityController!!.setNodePosition(protoNode!!, position)
        /*protoNode!!.position.x = position.x
        protoNode!!.position.y = position.y
        protoNode!!.position.z = position.z*/

        protoNode!!.orientation.x = orientation.x
        protoNode!!.orientation.y = orientation.y
        protoNode!!.orientation.z = orientation.z
        protoNode!!.orientation.w = orientation.w

        protoNode!!.scale.x = scale
        protoNode!!.scale.y = scale
        protoNode!!.scale.z = scale

        println("Adding node.")

        inputDevice.unselectNodes()

        virtualRealityController!!.networkClient!!.send(listOf(protoNode!!))
    }

    override fun active() {
        updateDisplay()
        updateObject()
    }

    override fun render() {

    }

    override fun deactive() {
        if (protoObject != null) {
            inputDevice.entity.remove(protoObject!!)
        }
        gripped = false
    }

    override fun onPressed(button: InputButton) {
        if (button == InputButton.GRIP) {
            gripped = true
        }
        //println("Pressed: $button")
    }

    override fun onReleased(button: InputButton) {
        if (button == InputButton.GRIP) {
            gripped = false
        }

        if (gripped) {
            if (button == InputButton.UP) {
                scale *= 2
                updateDisplay()
                updateObject()
            }
            if (button == InputButton.DOWN) {
                scale /= 2
                if (scale <= 0.125) {
                    scale = 0.125
                }
                updateDisplay()
                updateObject()
            }
            return
        }

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

        if (button == InputButton.TRIGGER) {
            addObject()
        }
    }

    override fun onSqueezed(button: InputButton, value: Double) {
        //println("Squeezed: $button $value")
    }

    override fun onPadTouched(x: Double, y: Double) {
        //println("Pad touched: $x,$y")
    }

}