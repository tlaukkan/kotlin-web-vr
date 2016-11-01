package vr.webvr.devices

import lib.threejs.Matrix4
import lib.threejs.Object3D
import vr.util.floatsToDoubles
import lib.webvrapi.Gamepad
import lib.webvrapi.getGamepads
import lib.webvrapi.navigator
import kotlin.browser.window

/**
 * The VR controller object.
 *
 * @author Tommi S.E. Laukkanen / https://github.com/tlaukkan
 * @author mrdoob / http://mrdoob.com/
 */
abstract class InputDevice(index: Int, type: String) {
    /**
     * The controller ID.
     */
    val index = index
    /**
     * The controller type.
     */
    val type = type
    /**
     * The standaing matrix.
     * @type {THREE.Matrix4}
     */
    var standingMatrix = Matrix4()
    /**
     * The game pad.
     */
    var gamepad: Gamepad? = null
    /**
     * The entity
     */
    val entity: Object3D = Object3D()

    init {
        this.entity.matrixAutoUpdate = false
        this.render()
    }

    abstract fun processInput()

    /**
     * Updates object state according controller physical state.
     */
    fun render() : Unit {
        window.requestAnimationFrame( { this.render()})

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
                //this.processInput()
            }

            this.entity.visible = true
        }

    }

}

