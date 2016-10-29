import network.NetworkClient
import threejs.MeshPhongMaterial
import threejs.Object3D
import webvr.*

fun main(args: Array<String>) {
    println("VR client startup...")

    //val m = PrimitiveHashMap<Int>(js("({a: 1, b: {c: 3, d: 4}})"))
    //println(m)

    val client = NetworkClient("ws://localhost:8080/ws")

    client.onConnected = { handshakeResponse ->
        println("Connected " + client.url + " (" + handshakeResponse.software + ")")
    }

    client.onReceive = { value ->
        println("Received value: $value")
    }

    client.onDisconnected = {
        println("Disconnected")
    }

    val displayDeviceController = DisplayDeviceController()

    displayDeviceController.startup({
        val renderer: Renderer
        val displayController: DisplayController
        val inputDeviceController: InputDeviceController
        val mediaController: MediaController
        val inputController: InputController
        try {
            renderer = Renderer()
            displayController = DisplayController(displayDeviceController, renderer)
            inputDeviceController = InputDeviceController(displayController)
            mediaController = MediaController()
            inputController = InputController(inputDeviceController)

            loadMedia(inputDeviceController, mediaController)
        } catch (t: Throwable) {
            println("VR client startup error: $t")
            return@startup
        }

        fun render(time: Number): Unit {
            var timeMillis = time.toLong()
            displayDeviceController.display!!.requestAnimationFrame(::render)
            renderer.render(timeMillis)
            displayController.render(renderer.scene, renderer.camera)
        }

        displayDeviceController.display!!.requestAnimationFrame(::render)
    }, { error ->
        println("VR clinet startup error: $error")
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

