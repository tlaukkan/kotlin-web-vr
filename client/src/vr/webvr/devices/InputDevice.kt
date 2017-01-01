package vr.webvr.devices

import lib.threejs.*
import vr.util.floatsToDoubles
import lib.webvrapi.Gamepad
import lib.webvrapi.getGamepads
import lib.webvrapi.navigator
import vr.webvr.tools.AddTool
import vr.webvr.tools.VoidTool
import vr.webvr.tools.MoveTool
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

    var activeTool: Tool = MoveTool(this)
    val tools: MutableList<Tool> = mutableListOf()
    var gamepad: Gamepad? = null

    val entity: Object3D = Object3D()
    val display: Object3D

    var standingMatrix = Matrix4()

    private var displayCanvasWidth = 1024
    private var displayCanvasHeight = 1024
    private val displayBitmap: dynamic
    private val displayTexture: dynamic

    var onSqueezed : ((button: InputButton, value: Double) -> Unit)? = null
    var onPressed: ((button: InputButton) -> Unit)? = null
    var onReleased: ((button: InputButton) -> Unit)? = null
    var onPadTouched: ((x: Double, y: Double) -> Unit)? = null
    var pressedButtons: MutableList<InputButton> = mutableListOf()


    init {
        this.entity.matrixAutoUpdate = false
        this.render()

        var displayCanvas : dynamic = document.createElement("canvas")
        displayCanvas.width = displayCanvasWidth
        displayCanvas.height = displayCanvasHeight

        displayBitmap = displayCanvas.getContext("2d")
        displayBitmap.font = "Normal 80px Arial"

        displayTexture = Texture(displayCanvas)
        displayTexture.needsUpdate = true
        var material = MeshBasicMaterial( object {var map = displayTexture} )
        material.transparent = true

        val planeGeometry = PlaneGeometry(0.3, 0.3)
        display = Mesh(planeGeometry, material)
        display.position.z = -0.15
        display.position.y = 0.15
        display.rotateX(-45 * 2 * Math.PI / 360)

        entity.add(display)

        tools.add(AddTool(this))
        tools.add(MoveTool(this))

        activateTool(tools[0])
    }

    fun activateTool(tool: Tool) {
        activeTool.deactive()
        activeTool = tool
        display(activeTool.name)
        activeTool.active()
        onPressed = { button ->
            tool.onPressed(button)
        }
        onReleased = { button ->
            tool.onReleased(button)
        }
        onSqueezed = { button, value ->
            tool.onSqueezed(button, value)
        }
        onPadTouched = { x, y ->
            tool.onPadTouched(x, y)
        }
    }

    fun display(text: String) {
        displayBitmap.clearRect(0, 0, displayCanvasWidth, displayCanvasHeight)
        displayBitmap.fillStyle = "rgba(0,0,0,0.2)"
        displayBitmap.fillRect(0, 0, displayCanvasWidth, displayCanvasHeight)
        displayBitmap.fillStyle = "rgba(0,0,0,0.4)"

        val lines = text.split('\n')
        var lineNumber = 1
        for (line in lines) {
            displayBitmap.fillText(line, 10, 80 * lineNumber)
            displayTexture.needsUpdate = true
            lineNumber ++
        }
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
        if (button == InputButton.MENU) {
            return
        }
        if (onSqueezed != null) {
            onSqueezed!!(button, value)
        }
    }

    fun pressed(button: InputButton) {
        if (button == InputButton.MENU) {
            return
        }
        if (onPressed != null) {
            onPressed!!(button)
        }
    }

    fun released(button: InputButton) {
        if (button == InputButton.MENU) {
            println("Changing active tool from ${activeTool.name}")
            var toolIndex = tools.indexOf(activeTool)
            toolIndex++
            if (toolIndex == tools.size) {
                toolIndex = 0
            }
            activateTool(tools[toolIndex])
            println("Changed active tool to ${activeTool.name}")
            return
        }
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

