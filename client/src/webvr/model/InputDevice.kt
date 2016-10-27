package webvr.model

import threejs.Matrix4
import threejs.Object3D
import webvr.floatsToDoubles
import webvrapi.Gamepad
import webvrapi.getGamepads
import webvrapi.navigator
import kotlin.browser.window

/**
 * The VR controller object.
 *
 * @author Tommi S.E. Laukkanen / https://github.com/tlaukkan
 * @author mrdoob / http://mrdoob.com/
 */
class InputDevice(index: Int, type: String, handler: (controller: InputDevice) -> Unit) : Object3D() {
    /**
     * The controller ID.
     */
    val index = index
    /**
     * The controller type.
     */
    val type = type
    /**
     * The controller handler.
     */
    val handler = handler
    /**
     * The standaing matrix.
     * @type {THREE.Matrix4}
     */
    var standingMatrix = Matrix4()
    /**
     * The game pad.
     */
    var gamepad: Gamepad? = null

    init {
        this.matrixAutoUpdate = false
        this.update()
    }

    /**
     * Updates object state according controller physical state.
     */
    fun update() : Unit {
        // TODO integrate this with main update loop
        window.requestAnimationFrame( { this.update()})

        var gamepad: Gamepad = navigator.getGamepads()[this.index]

        if (gamepad !== undefined) {

            var pose = gamepad.pose

            if (pose != null && pose != undefined) {
                this.position.fromArray(floatsToDoubles(pose.position).toTypedArray())
                this.quaternion.fromArray(floatsToDoubles(pose.orientation).toTypedArray())
                this.matrix.compose(this.position, this.quaternion, this.scale)
                this.matrix.multiplyMatrices(this.standingMatrix, this.matrix)
                this.matrixWorldNeedsUpdate = true

                this.gamepad = gamepad
                this.handler(this)
            }

            this.visible = true
        }

    }

}

