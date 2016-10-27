import threejs.MeshBasicMaterial
import threejs.MeshPhongMaterial
import threejs.Object3D
import webvr.*
import webvr.model.Controller
import kotlin.browser.window

fun main(args: Array<String>) {
    println("VR client startup...")

    val virtualRealityController = VirtualRealityController()

    virtualRealityController.startup({
        val graphicsController = GraphicsController()
        val displayController = DisplayController(virtualRealityController, graphicsController)
        val controllerController = ControllerController(displayController)
        val mediaController = MediaController()
        controllerController.controllerHandlers["OpenVR Gamepad"] = ::handleViveController

        var vivePath = "models/obj/vive-controller/"
        mediaController.loadModel("OpenVR Gamepad", vivePath + "vr_controller_vive_1_5.obj", { name, model ->
            var controller: Object3D = model.children[0]

            mediaController.loadTexture(vivePath + "onepointfive_texture.png", { name, texture ->
                (controller.material as MeshPhongMaterial).map = texture
            })
            mediaController.loadTexture(vivePath + "onepointfive_spec.png", { name, texture ->
                (controller.material as MeshPhongMaterial).specularMap = texture
            })

            controllerController.controllerModels["OpenVR Gamepad"] = model
        })

        fun render(time: Double) : Unit {
            graphicsController.render(time)
            displayController.render(graphicsController.scene, graphicsController.camera)
            window.requestAnimationFrame(::render)
        }
        render(1.0)

    }, { error ->
        println("Startup interrupted: " + error)
    })
}

fun handleViveController(controller: Controller) {
    var gamepad = controller.gamepad!!
    var padTouched: Boolean = false
    for (button in gamepad.buttons) {
        var i = gamepad.buttons.indexOf(button)
        if (button.pressed) {
            console.log("Button $i pressed with value: ${button.value}")
        }
        if (button.touched) {
            console.log("Button $i touched with value: ${button.value}")
        }
        if (i == 0 && button.touched) {
            padTouched = true
        }
    }

    var axes = gamepad.axes
    for (axis in axes) {
        var i = axes.indexOf(axis)
        if (padTouched) {
            console.log("Axis $i: $axis")
        }
    }
}