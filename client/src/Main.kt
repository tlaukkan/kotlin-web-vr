import vr.network.NetworkClient
import lib.threejs.MeshPhongMaterial
import lib.threejs.Object3D
import lib.threejs.Vector3
import org.w3c.dom.svg.SVGAnimatedInteger
import vr.network.model.LinkRequest
import vr.webvr.*
import java.util.*
import kotlin.browser.window

var renderTime: Double = 0.0
var renderTimeDelta: Double = 0.001

fun main(args: Array<String>) {
    println("VR client startup...")

    val displayDeviceController = DisplayDeviceController()

    displayDeviceController.startup({
        val rendererController: RendererController
        val displayController: DisplayController
        val inputDeviceController: InputDeviceController
        val mediaController: MediaController
        val virtualRealityController: VirtualRealityController
        try {
            rendererController = RendererController()
            displayController = DisplayController(displayDeviceController, rendererController)
            inputDeviceController = InputDeviceController(displayController)
            mediaController = MediaController()
            virtualRealityController = VirtualRealityController(displayController, mediaController)

            val location = window.location
            val client: NetworkClient
            if (location.port != null && location.port != undefined) {
                client = NetworkClient("ws://${location.hostname}:${location.port}/ws")
            } else {
                client = NetworkClient("ws://${location.hostname}/ws")
            }

            client.onConnected = { handshakeResponse ->
                println("Connected " + client.url + " (" + handshakeResponse.software + ")")
                client.send(listOf(LinkRequest(arrayOf(), arrayOf(handshakeResponse.serverCellUris[0]))))
            }

            client.onLinked = { linkResponse ->
                virtualRealityController.neighbours.clear()
                for (neighbour in linkResponse.neighbours) {
                    virtualRealityController.neighbours[neighbour.cellUriTwo] = Vector3(
                            neighbour.oneTwoDeltaVector.x,
                            neighbour.oneTwoDeltaVector.y,
                            neighbour.oneTwoDeltaVector.z)
                }

                println("Linked to server cell: " + linkResponse.serverCellUris[0])
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

            if (renderTime != 0.0) {
                renderTimeDelta = timeMillis / 1000.0 - renderTime
            }
            renderTime = timeMillis / 1000.0

            displayDeviceController.display!!.requestAnimationFrame(::render)

            inputDeviceController.render()
            virtualRealityController.update()
            rendererController.render()
            displayController.render(rendererController.scene, rendererController.camera)
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

    /*mediaController.loadModel("models/collada/monster/monster.dae", { path, model ->
        var monster = model.clone(true)
        monster.scale.x = 0.002
        monster.scale.y = 0.002
        monster.scale.z = 0.002

        monster.position.x = 0.0
        monster.position.y = 0.0
        monster.position.z = 5.0
        displayController.scene.add(monster)
    })*/

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

