import threejs.MeshBasicMaterial
import threejs.MeshPhongMaterial
import threejs.Object3D
import webvr.*
import webvr.model.InputDevice
import kotlin.browser.window

fun main(args: Array<String>) {
    println("VR client startup...")

    val displayDeviceController = DisplayDeviceController()

    displayDeviceController.startup({
        val renderer = Renderer()
        val displayController = DisplayController(displayDeviceController, renderer)
        val inputDeviceController = InputDeviceController(displayController)
        val mediaController = MediaController()
        val inputController = InputController(inputDeviceController)

        loadMedia(inputDeviceController, mediaController)

        fun render(time: Number) : Unit {
            displayDeviceController.display!!.requestAnimationFrame(::render)
            renderer.render(time.toLong())
            displayController.render(renderer.scene, renderer.camera)
        }
        render(1.0)

    }, { error ->
        println("Startup interrupted: " + error)
    })
}

private fun loadMedia(inputDeviceController: InputDeviceController, mediaController: MediaController) {
    var vivePath = "models/obj/vive-controller/"
    mediaController.loadModel(vivePath + "vr_controller_vive_1_5.obj", { path, model ->
        var inputDeviceModel: Object3D = model.children[0]

        mediaController.loadTexture(vivePath + "onepointfive_texture.png", { path, texture ->
            (inputDeviceModel.material as MeshPhongMaterial).map = texture
        })
        mediaController.loadTexture(vivePath + "onepointfive_spec.png", { path, texture ->
            (inputDeviceModel.material as MeshPhongMaterial).specularMap = texture
        })

        inputDeviceController.inputDeviceModels["OpenVR Gamepad"] = model
    })
}

