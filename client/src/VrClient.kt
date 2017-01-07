import vr.network.NetworkClient
import lib.threejs.Vector3
import lib.webvrapi.VRDisplay
import vr.network.RestClient
import vr.network.model.LinkRequest
import vr.webvr.*
import kotlin.browser.window

class VrClient(val display: VRDisplay) {

    var renderTime: Double = 0.0
    var renderTimeDelta: Double = 0.001

    val vrController: VirtualRealityController
    val rendererController: RendererController
    val displayController: DisplayController
    val inputDeviceController: InputDeviceController
    val mediaController: MediaController
    val restClient: RestClient

    init {
        CLIENT = this
        println("VR client startup...")

        rendererController = RendererController()
        displayController = DisplayController(display, rendererController)
        inputDeviceController = InputDeviceController(displayController)
        mediaController = MediaController()
        vrController = VirtualRealityController(displayController, mediaController)

        val location = window.location
        val networkClient: NetworkClient
        if (location.port != null && location.port != undefined) {
            networkClient = NetworkClient("ws://${location.hostname}:${location.port}/ws")
            restClient = RestClient("http://${location.hostname}:${location.port}/api")
        } else {
            networkClient = NetworkClient("ws://${location.hostname}/ws")
            restClient = RestClient("http://${location.hostname}/api")
        }

        vrController!!.networkClient = networkClient

        networkClient.onConnected = { handshakeResponse ->
            println("Connected " + networkClient.url + " (" + handshakeResponse.software + ")")
            networkClient.send(listOf(LinkRequest(arrayOf(), arrayOf(handshakeResponse.serverCellUris[0]))))
        }

        networkClient.onLinked = { linkResponse ->
            vrController!!.neighbours.clear()
            for (neighbour in linkResponse.neighbours) {
                vrController!!.neighbours[neighbour.cellUriTwo] = Vector3(
                        neighbour.oneTwoDeltaVector.x,
                        neighbour.oneTwoDeltaVector.y,
                        neighbour.oneTwoDeltaVector.z)
            }
            vrController!!.linkedServerCellUrl = linkResponse.serverCellUris[0]
            println("Linked to server cell: " + linkResponse.serverCellUris[0])
        }

        networkClient.onReceive = { type, value ->
            vrController!!.onReceive(type, value)
        }

        networkClient.onDisconnected = {
            println("Disconnected")
        }

        mediaController.loadMedia(displayController, inputDeviceController, mediaController)

        display.requestAnimationFrame({ time -> render(time) })

    }

    fun render(time: Number): Unit {
        var timeMillis = time.toLong()

        if (renderTime != 0.0) {
            renderTimeDelta = timeMillis / 1000.0 - renderTime
        }
        renderTime = timeMillis / 1000.0

        display.requestAnimationFrame({ time -> render(time) })

        inputDeviceController.render()
        vrController.update()
        rendererController.render()
        displayController.render(rendererController.scene, rendererController.camera)
    }
}
