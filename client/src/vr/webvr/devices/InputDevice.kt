package vr.webvr.devices

import lib.threejs.Matrix4
import lib.threejs.Object3D
import vr.util.floatsToDoubles
import lib.webvrapi.Gamepad
import lib.webvrapi.getGamepads
import lib.webvrapi.navigator
import vr.webvr.tools.Tool
import kotlin.browser.window

/**
 * The VR controller object.
 *
 * @author mrdoob / http://mrdoob.com/
 * @author Tommi S.E. Laukkanen / https://github.com/tlaukkan
 */
abstract class InputDevice(index: Int, type: String) {

    val index = index
    val type = type

    val tools: MutableList<Tool> = mutableListOf()
    var gamepad: Gamepad? = null
    val entity: Object3D = Object3D()

    var onSqueezed : ((button: InputButton, value: Double) -> Unit)? = null
    var onPressed: ((button: InputButton) -> Unit)? = null
    var onReleased: ((button: InputButton) -> Unit)? = null
    var onPadTouched: ((x: Double, y: Double) -> Unit)? = null

    var pressedButtons: MutableList<InputButton> = mutableListOf()

    var standingMatrix = Matrix4()

    init {
        this.entity.matrixAutoUpdate = false
        this.render()
    }

    /**
     * Updates object state according controller physical state.
     */
    fun render() : Unit {

        var gamepad: Gamepad = navigator.getGamepads()[this.index]

        if (gamepad !== undefined) {

            var pose = gamepad.pose

            if (pose != null && pose != undefined) {
                this.entity.position.fromArray(floatsToDoubles(pose.position).toTypedArray())
                this.entity.quaternion.fromArray(floatsToDoubles(pose.orientation).toTypedArray())
                this.entity.matrix.compose(this.entity.position, this.entity.quaternion, this.entity.scale)
                this.entity.matrix.multiplyMatrices(this.standingMatrix, this.entity.matrix)
                this.entity.matrixWorldNeedsUpdate = true
                this.gamepad = gamepad
            }

            this.entity.visible = true
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

    abstract fun processInput()

}

