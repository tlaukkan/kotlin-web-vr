package vr.webvr.devices

import lib.threejs.*
import vr.util.floatsToDoubles
import lib.webvrapi.Gamepad
import lib.webvrapi.getGamepads
import lib.webvrapi.navigator
import vr.webvr.tools.Tool
import kotlin.browser.document
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
    val display: Object3D

    var standingMatrix = Matrix4()

    private var displayCanvasWidth = 1024
    private var Canvas = 1024
    private val displayBitmap: dynamic

    var onSqueezed : ((button: InputButton, value: Double) -> Unit)? = null
    var onPressed: ((button: InputButton) -> Unit)? = null
    var onReleased: ((button: InputButton) -> Unit)? = null
    var onPadTouched: ((x: Double, y: Double) -> Unit)? = null
    var pressedButtons: MutableList<InputButton> = mutableListOf()


    init {
        this.entity.matrixAutoUpdate = false
        this.render()


        var displayCanvas : dynamic = document.createElement("canvas")
        displayCanvas.w
        displayCanvas.width = displayCanvasWidth
        displayCanvas.height = Canvas

        displayBitmap = displayCanvas.getContext("2d")
        displayBitmap.font = "Normal 80px Arial"

        var displayTexture : dynamic = Texture(displayCanvas)
        displayTexture.needsUpdate = true
        var material = MeshBasicMaterial( object {var map = displayTexture} )
        material.transparent = true

        val planeGeometry = PlaneGeometry(0.3, 0.3)
        display = Mesh(planeGeometry, material)
        display.position.z = -0.15
        display.position.y = 0.15
        display.rotateX(-45 * 2 * Math.PI / 360)

        display("Hello")

        entity.add(display)
    }

    fun display(text: String) {
        displayBitmap.clearRect(0,0, displayCanvasWidth, Canvas)
        displayBitmap.fillStyle = "rgba(0,0,0,0.2)"
        displayBitmap.fillRect(0,0, displayCanvasWidth, Canvas)
        displayBitmap.fillStyle = "rgba(0,0,0,0.4)"
        displayBitmap.fillText(text, 10, 80)
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

