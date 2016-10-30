import vr.network.NetworkClient
import lib.threejs.MeshPhongMaterial
import lib.threejs.Object3D
import lib.threejs.Vector3
import vr.network.model.CellSelectRequest
import vr.webvr.*

fun main(args: Array<String>) {
    println("VR client startup...")

    val displayDeviceController = DisplayDeviceController()

    displayDeviceController.startup({
        val renderer: Renderer
        val displayController: DisplayController
        val inputDeviceController: InputDeviceController
        val mediaController: MediaController
        val inputController: InputController
        val virtualRealityController: VirtualRealityController
        try {
            renderer = Renderer()
            displayController = DisplayController(displayDeviceController, renderer)
            inputDeviceController = InputDeviceController(displayController)
            mediaController = MediaController()
            inputController = InputController(inputDeviceController)
            virtualRealityController = VirtualRealityController(displayController)

            val client = NetworkClient("ws://localhost:8080/ws")

            client.onConnected = { handshakeResponse ->
                println("Connected " + client.url + " (" + handshakeResponse.software + ")")
                client.send(listOf(CellSelectRequest(handshakeResponse.cellNames[0])))
            }

            client.onCellSelected = { cellSelectResponse ->
                println("Cell selected: " + cellSelectResponse.cellName)
            }

            client.onReceive = { type, value ->
                virtualRealityController.onReceive(type, value)
            }

            client.onDisconnected = {
                println("Disconnected")
            }

            loadMedia(displayController, inputDeviceController, mediaController)
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

private fun loadMedia(displayController: DisplayController, inputDeviceController: InputDeviceController, mediaController: MediaController) {
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

    mediaController.loadModel("models/collada/monster/monster.dae", { path, model ->
        var monster = model.clone(true)
        monster.scale.x = 0.002
        monster.scale.y = 0.002
        monster.scale.z = 0.002

        monster.position.x = 0.0
        monster.position.y = 0.0
        monster.position.z = 5.0
        displayController.scene.add(monster)
    })

    mediaController.loadModel("models/animated/monster/monster.js", { path, model ->
        var monster = model.clone(true)
        monster.scale.x = 0.002
        monster.scale.y = 0.002
        monster.scale.z = 0.002

        monster.position.x = -10.0
        monster.position.y = 0.0
        monster.position.z = 0.0
        displayController.scene.add(monster)
    })
}

