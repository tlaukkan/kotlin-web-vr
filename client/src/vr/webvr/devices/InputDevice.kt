package vr.webvr.devices

import vr.CLIENT
import lib.threejs.*
import lib.threejs.Extra.BoxGeometry
import lib.threejs.Extra.SphereGeometry
import vr.util.floatsToDoubles
import lib.webvrapi.Gamepad
import lib.webvrapi.getGamepads
import lib.webvrapi.navigator
import vr.client.VrClient
import vr.webvr.tools.*
import kotlin.browser.document

/**
 * The VR controller object.
 *
 * @author mrdoob / http://mrdoob.com/
 * @author Tommi S.E. Laukkanen / https://github.com/tlaukkan
 */
abstract class InputDevice(index: Int, type: String) {

    val index = index
    val type = type

    var addedToScene = false

    var lastActiveToolIndex = 0

    var activeTool: Tool = NoneTool(this)
    val menuTool: MenuTool = MenuTool(this)

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

    var selectLine: Line? = null
    val selectedNodeUrls: MutableList<String> = mutableListOf()

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

        val travelTool = TravelTool(this)

        tools.add(travelTool)
        tools.add(RotateTool(this))
        tools.add(BuildTool(this))
        tools.add(HandTool(this))
        tools.add(AddTool(this))
        tools.add(RemoveTool(this))

        val activeToolIndex = (index + 1) % 2

        val rightController = tools[activeToolIndex] != travelTool

        val planeGeometry = PlaneGeometry(0.3, 0.3)
        display = Mesh(planeGeometry, material)
        display.rotateX(-90 * 2 * Math.PI / 360)
        if (rightController) {
            display.rotateZ(-90 * 2 * Math.PI / 360)
        } else {
            display.rotateZ(90 * 2 * Math.PI / 360)
        }
        display.position.x = 0.3
        if (rightController) {
            display.position.x = 0.3
        } else {
            display.position.x = -0.3
        }
        display.position.z = 0.15

        entity.add(display)

        activateTool(tools[activeToolIndex])

    }

    fun activateToolByClass(toolClassName: String) {
        for (tool in tools) {
            println("Mathing tool class ${toolClassName} with ${tool.jsClass.name}")
            if (tool != activeTool && tool.jsClass.name.equals(toolClassName)) {
                println("Activating: ${tool.jsClass.name}")
                activateTool(tool)
            }
        }
    }

    fun activateTool(tool: Tool) {
        lastActiveToolIndex = tools.indexOf(activeTool)
        activeTool.deactivate()
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
        displayBitmap.fillStyle = "rgba(0,128,128,0.1)"
        displayBitmap.fillRect(0, 0, displayCanvasWidth, displayCanvasHeight)
        displayBitmap.fillStyle = "rgba(0,255,255,0.3)"

        val lines = text.split('\n')
        var lineNumber = 1
        for (line in lines) {
            displayBitmap.fillText(line, 10, 80 * lineNumber)
            displayTexture.needsUpdate = true
            lineNumber ++
        }
    }

    fun showSelectLine(lineColor : Int) {
        val geometry = Geometry()
        geometry.vertices = arrayOf(
                Vector3(0.0, 0.0, 0.0),
                Vector3(0.0, 0.0, -100.0))

        val material = MeshBasicMaterial(object { var color: Int = lineColor })
        material.opacity = 0.3
        material.transparent = true
        selectLine = Line(geometry, material)
        entity.add(selectLine!!)
    }

    fun hideSelectLine() {
        entity.remove(selectLine!!)
    }

    fun unselectNodes() {
        for (selectedNodeUrl in selectedNodeUrls) {
            val selectedObject = CLIENT!!.vrController.scene.getObjectByName(selectedNodeUrl)
            if (selectedObject != null) {
                val selectorObject = CLIENT!!.vrController.scene.getObjectByName(selectedObject.name + "-selector")
                if (selectorObject != null) {
                    selectedObject.remove(selectorObject)
                }
            }

            //val node = CLIENT!!.vrController.nodes[selectedNodeUrl]
            /*if (obj != null) {
                val material = obj.material
                material.transparent = false
                if (node != null) {
                    material.opacity = node.opacity
                }
            }*/
        }
        selectedNodeUrls.clear()
    }

    fun selectNodes() : Double? {
        val origin = Vector3()
        entity.getWorldPosition(origin)
        val orientation = Quaternion(0.0, 0.0, 0.0, 1.0)
        entity.getWorldQuaternion(orientation)

        val direction = Vector3(0.0, 0.0, -1.0)
        direction.applyQuaternion(orientation)

        val raycaster = Raycaster()
        raycaster.set(origin, direction)

        val results = raycaster.intersectObjects(CLIENT!!.vrController.scene.children, true)

        for (result in results) {
            val distance: Double = result["distance"]
            val obj: Object3D = result["object"]
            if (CLIENT!!.vrController.nodes.containsKey(obj.name)) {
                select(obj.name)
                return distance
            }
        }

        return null
    }

    fun rayNodes() : Double? {
        val origin = Vector3()
        entity.getWorldPosition(origin)
        val orientation = Quaternion(0.0, 0.0, 0.0, 1.0)
        entity.getWorldQuaternion(orientation)

        val direction = Vector3(0.0, 0.0, -1.0)
        direction.applyQuaternion(orientation)

        val raycaster = Raycaster()
        raycaster.set(origin, direction)

        val results = raycaster.intersectObjects(CLIENT!!.vrController.scene.children, true)

        for (result in results) {
            val distance: Double = result["distance"]
            val obj: Object3D = result["object"]
            if (CLIENT!!.vrController.nodes.containsKey(obj.name)) {
                return distance
            }
        }

        return null
    }

    private fun select(selectedNodeUrl: String) {
        val selectedNode = CLIENT!!.vrController.nodes[selectedNodeUrl] ?: return
        val selectedObject = CLIENT!!.vrController.scene.getObjectByName(selectedNode.url) ?: return
        /*val material = selectedObject.material
        material.transparent = true
        material.opacity = 0.5*/

        var geometry = BoxGeometry(1.1 * selectedNode.scale.x, 1.1 * selectedNode.scale.y, 1.1 * selectedNode.scale.z)

        val material = MeshBasicMaterial()
        material.transparent = true
        material.color = Color(0x00ffff)
        material.opacity = 0.1
        material.wireframe = true

        var selectorObject = Mesh(geometry, material)
        selectorObject.castShadow = false
        selectorObject.receiveShadow = false
        selectorObject.name = selectedObject.name + "-selector"
        selectorObject.scale.x = 1 / selectedObject.scale.x
        selectorObject.scale.y = 1 / selectedObject.scale.y
        selectorObject.scale.z = 1 / selectedObject.scale.z

        selectedObject.add(selectorObject)

        selectedNodeUrls.add(selectedObject.name)
        println("Selected object: ${selectedObject.uuid} ${selectedObject.name} ${selectedObject.parent}")
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

        this.activeTool.render()

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
            if (activeTool == menuTool) {
                activateTool(tools[menuTool.toolIndex])
            } else {
                activateTool(menuTool)
            }
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

