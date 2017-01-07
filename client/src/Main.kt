import vr.network.NetworkClient
import lib.threejs.Vector3
import vr.network.RestClient
import vr.network.model.LinkRequest
import vr.webvr.*
import kotlin.browser.window

var renderTime: Double = 0.0
var renderTimeDelta: Double = 0.001
var virtualRealityController: VirtualRealityController? = null

fun main(args: Array<String>) {
    println("VR client startup...")

    val displayDeviceController = DisplayDeviceController()

    displayDeviceController.startup({

        val rendererController: RendererController
        val displayController: DisplayController
        val inputDeviceController: InputDeviceController
        val mediaController: MediaController

        rendererController = RendererController()
        displayController = DisplayController(displayDeviceController, rendererController)
        inputDeviceController = InputDeviceController(displayController)
        mediaController = MediaController()
        virtualRealityController = VirtualRealityController(displayController, mediaController)

        val location = window.location
        val networkClient: NetworkClient
        if (location.port != null && location.port != undefined) {
            networkClient = NetworkClient("ws://${location.hostname}:${location.port}/ws")
            virtualRealityController!!.restClient = RestClient("http://${location.hostname}:${location.port}/api")
        } else {
            networkClient = NetworkClient("ws://${location.hostname}/ws")
            virtualRealityController!!.restClient = RestClient("http://${location.hostname}/api")
        }

        virtualRealityController!!.networkClient = networkClient

        networkClient.onConnected = { handshakeResponse ->
            println("Connected " + networkClient.url + " (" + handshakeResponse.software + ")")
            networkClient.send(listOf(LinkRequest(arrayOf(), arrayOf(handshakeResponse.serverCellUris[0]))))
        }

        networkClient.onLinked = { linkResponse ->
            virtualRealityController!!.neighbours.clear()
            for (neighbour in linkResponse.neighbours) {
                virtualRealityController!!.neighbours[neighbour.cellUriTwo] = Vector3(
                        neighbour.oneTwoDeltaVector.x,
                        neighbour.oneTwoDeltaVector.y,
                        neighbour.oneTwoDeltaVector.z)
            }
            virtualRealityController!!.linkedServerCellUrl = linkResponse.serverCellUris[0]
            println("Linked to server cell: " + linkResponse.serverCellUris[0])
        }

        networkClient.onReceive = { type, value ->
            virtualRealityController!!.onReceive(type, value)
        }

        networkClient.onDisconnected = {
            println("Disconnected")
        }

        mediaController.loadMedia(displayController, inputDeviceController, mediaController)

        fun render(time: Number): Unit {
            var timeMillis = time.toLong()

            if (renderTime != 0.0) {
                renderTimeDelta = timeMillis / 1000.0 - renderTime
            }
            renderTime = timeMillis / 1000.0

            displayDeviceController.display!!.requestAnimationFrame(::render)

            inputDeviceController.render()
            virtualRealityController!!.update()
            rendererController.render()
            displayController.render(rendererController.scene, rendererController.camera)
        }

        displayDeviceController.display!!.requestAnimationFrame(::render)
    }, { error ->
        println("VR clinet startup error: $error")
    })
}

