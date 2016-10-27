import org.w3c.dom.Element
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.Node
import threejs.*
import webvrapi.VRDisplay
import webvrapi.VRFieldOfView
import webvrapi.VRLayer
import kotlin.browser.document
import kotlin.browser.window
import kotlin.dom.addClass
import kotlin.dom.onClick

class DisplayController(virtualRealityController: VirtualRealityController, graphicsController: GraphicsController) {

    var eyeTranslationL = Vector3()
    var eyeTranslationR = Vector3()
    var renderRectL = Rectangle()
    var renderRectR = Rectangle()
    var eyeFOVL: VRFieldOfView? = null
    var eyeFOVR: VRFieldOfView? = null
    var isPresenting = false
    var scale = 1.0
    var rendererWidth: Double
    var rendererHeight: Double
    var rendererPixelRatio: Double

    val display: VRDisplay
    val renderer: WebGLRenderer
    val canvas: Element


    val cameraL: PerspectiveCamera
    val cameraR: PerspectiveCamera

    init {
        addEnterVrButton()

        display = virtualRealityController.display!!
        renderer = graphicsController.renderer

        canvas = renderer.domElement;

        rendererWidth = window.innerWidth
        rendererHeight = window.innerHeight
        rendererPixelRatio = window.devicePixelRatio

        window.addEventListener("vrdisplaypresentchange", { onVrDisplayPresentChange() }, false)

        // render

        this.cameraL = PerspectiveCamera(0.0,0.0,0.0,0.0)
        this.cameraR = PerspectiveCamera(0.0,0.0,0.0,0.0)
        //this.cameraL.layers.enable(1)
        //this.cameraR.layers.enable(2)
    }

    fun setSize(width: Double, height: Double) {
        rendererWidth = width
        rendererHeight = height
        if (this.isPresenting) {
            var eyeParamsL = this.display.getEyeParameters("left")
            this.renderer.setPixelRatio(1)
            this.renderer.setSize(eyeParamsL.renderWidth.toDouble() * 2, eyeParamsL.renderHeight.toDouble())
        } else {
            this.renderer.setPixelRatio(this.rendererPixelRatio)
            this.renderer.setSize(width, height)
        }
    }

    fun setPresent(present: Boolean) {
        if (isPresenting === present) {
            return
        }
        if (present) {
            val vrLayer = object {
                var source = canvas
            }
            println("Starting to present VR...")
            display.requestPresent(arrayOf(vrLayer)).catch { error ->
                println("Failed to start presenting VR: $error")
            }.then {
                println("Started presenting VR.")
            }
        } else {
            display.exitPresent().catch { error ->
                println("Failed to stop presenting VR: $error")
            }. then {
                println("Stopped presenting VR.")
            }
        }
    }

    fun onVrDisplayPresentChange() {

        if (isPresenting == display.isPresenting) {
            return
        }

        isPresenting = display.isPresenting

        if (isPresenting) {
            rendererPixelRatio = renderer.getPixelRatio().toDouble()

            var eyeParamsL = this.display.getEyeParameters("left")
            var eyeWidth = eyeParamsL.renderWidth.toDouble()
            var eyeHeight = eyeParamsL.renderHeight.toDouble()

            renderer.setPixelRatio(1)
            renderer.setSize(eyeWidth * 2, eyeHeight)
        } else {
            renderer.setPixelRatio(this.rendererPixelRatio)
            renderer.setSize(rendererWidth, rendererHeight)
        }

    }


    fun requestPresent() {
        return this.setPresent(true)
    }

    fun exitPresent() {
        return this.setPresent(false)
    }

    fun render(scene: Scene, camera: PerspectiveCamera) {




        if (isPresenting) {

            var autoUpdate = scene.autoUpdate;

            if (autoUpdate) {

                scene.updateMatrixWorld()
                scene.autoUpdate = false

            }


            var eyeParamsL = this.display.getEyeParameters("left")
            var eyeParamsR = this.display.getEyeParameters("right")

            var offsetLeft: List<String> = eyeParamsL.offset.toString().split(",")
            this.eyeTranslationL.x = safeParseDouble(offsetLeft.get(0))!!
            this.eyeTranslationL.y = safeParseDouble(offsetLeft.get(1))!!
            this.eyeTranslationL.z = safeParseDouble(offsetLeft.get(2))!!
            var offsetRight: List<String> = eyeParamsR.offset.toString().split(",")
            this.eyeTranslationR.x = safeParseDouble(offsetRight.get(0))!!
            this.eyeTranslationR.y = safeParseDouble(offsetRight.get(0))!!
            this.eyeTranslationR.z = safeParseDouble(offsetRight.get(0))!!
            this.eyeFOVL = eyeParamsL.fieldOfView
            this.eyeFOVR = eyeParamsR.fieldOfView


            // When rendering we don't care what the recommended size is, only what the actual size
            // of the backbuffer is.
            this.renderRectL = Rectangle(0.0, 0.0, rendererWidth / 2, rendererHeight)
            this.renderRectR = Rectangle(rendererWidth / 2, 0.0, rendererWidth / 2, rendererHeight)

            this.renderer.setScissorTest(true)
            this.renderer.clear()

            if (camera.parent === null) camera.updateMatrixWorld()

            this.cameraL.projectionMatrix = this.fovToProjection(this.eyeFOVL!!, true, camera.near, camera.far)
            this.cameraR.projectionMatrix = this.fovToProjection(this.eyeFOVR!!, true, camera.near, camera.far)

            camera.matrixWorld.decompose(this.cameraL.position, this.cameraL.quaternion, this.cameraL.scale)
            camera.matrixWorld.decompose(this.cameraR.position, this.cameraR.quaternion, this.cameraR.scale)

            var scale = this.scale
            this.cameraL.translateOnAxis(this.eyeTranslationL, scale)
            this.cameraR.translateOnAxis(this.eyeTranslationR, scale)


            // render left eye
            this.renderer.setViewport(this.renderRectL.x, this.renderRectL.y, this.renderRectL.width, this.renderRectL.height)
            this.renderer.setScissor(this.renderRectL.x, this.renderRectL.y, this.renderRectL.width, this.renderRectL.height)
            this.renderer.render(scene, this.cameraL)

            // render right eye
            this.renderer.setViewport(this.renderRectR.x, this.renderRectR.y, this.renderRectR.width, this.renderRectR.height)
            this.renderer.setScissor(this.renderRectR.x, this.renderRectR.y, this.renderRectR.width, this.renderRectR.height)
            this.renderer.render(scene, this.cameraR)

            this.renderer.setScissorTest(false)

            if (autoUpdate) {

                scene.autoUpdate = true

            }

            this.display.submitFrame(display.getPose())

        } else {
            // Regular render mode if not HMD
            this.renderer.render(scene, camera)
        }

    }

    fun fovToProjection(fov: VRFieldOfView, rightHanded: Boolean, zNear_: Double, zFar_: Double): Matrix4 {

        val zNear: Double
        if (zNear_ === undefined) {
            zNear = 0.01
        } else {
            zNear = zNear_
        }
        val zFar: Double
        if (zFar_ === undefined) {
            zFar = 10000.0
        } else {
            zFar = zFar_
        }

        var DEG2RAD = Math.PI / 180.0

        var fovPort = FovPort(
                Math.tan(fov.upDegrees.toDouble() * DEG2RAD),
                Math.tan(fov.downDegrees.toDouble() * DEG2RAD),
                Math.tan(fov.leftDegrees.toDouble() * DEG2RAD),
                Math.tan(fov.rightDegrees.toDouble() * DEG2RAD)
        )

        return this.fovPortToProjection(fovPort, rightHanded, zNear, zFar)
    }

    fun fovPortToProjection(fov: FovPort, rightHanded: Boolean, zNear: Double, zFar: Double) : Matrix4 {

        val handednessScale: Double
        if (rightHanded) {
            handednessScale = -1.0
        } else {
            handednessScale = 1.0
        }

        // start with an identity matrix
        var mobj = Matrix4()
        var m = mobj.elements

        // and with scale/offset info for normalized device coords
        var scaleAndOffset = this.fovToNDCScaleOffset(fov)

        // X result, map clip edges to [-w,+w]
        m[0 * 4 + 0] = scaleAndOffset.pxscale
        m[0 * 4 + 1] = 0.0;
        m[0 * 4 + 2] = scaleAndOffset.pxoffset * handednessScale
        m[0 * 4 + 3] = 0.0;

        // Y result, map clip edges to [-w,+w]
        // Y offset is negated because this proj matrix transforms from world coords with Y=up,
        // but the NDC scaling has Y=down (thanks D3D?)
        m[1 * 4 + 0] = 0.0
        m[1 * 4 + 1] = scaleAndOffset.pyscale
        m[1 * 4 + 2] = -scaleAndOffset.pyoffset * handednessScale
        m[1 * 4 + 3] = 0.0

        // Z result (up to the app)
        m[2 * 4 + 0] = 0.0
        m[2 * 4 + 1] = 0.0
        m[2 * 4 + 2] = zFar / ( zNear - zFar ) * -handednessScale
        m[2 * 4 + 3] = ( zFar * zNear ) / ( zNear - zFar )

        // W result (= Z in)
        m[3 * 4 + 0] = 0.0
        m[3 * 4 + 1] = 0.0
        m[3 * 4 + 2] = handednessScale
        m[3 * 4 + 3] = 0.0

        mobj.transpose()

        return mobj

    }

    fun fovToNDCScaleOffset(fov: FovPort): NDCScaleOffset {
        var pxscale = 2.0 / ( fov.leftTan + fov.rightTan )
        var pxoffset = ( fov.leftTan - fov.rightTan ) * pxscale * 0.5
        var pyscale = 2.0 / ( fov.upTan + fov.downTan )
        var pyoffset = ( fov.upTan - fov.downTan ) * pyscale * 0.5
        return NDCScaleOffset(pxscale, pyscale, pxoffset, pyoffset)
    }


    private fun addEnterVrButton(): Node {
        var button = document.createElement("button")
        button.addClass("enter-vr-button")
        button.textContent = "ENTER VR"
        button.onClick {

            if (isPresenting) {
                exitPresent()
            } else {
                requestPresent()
            }
            // effect.isPresenting ? effect.exitPresent() : effect.requestPresent();
        }

        window.addEventListener("vrdisplaypresentchange", {
            if ("ENTER VR".equals(button.textContent)) {
                button.textContent = "EXIT VR"
            } else {
                button.textContent = "ENTER VR"
            }
        }, false)

        return document.body!!.appendChild(button)
    }
}

