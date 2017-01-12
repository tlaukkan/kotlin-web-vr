package vr.webvr

import CLIENT

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

    val vrController: VrController
    val rendererController: RendererController
    val displayController: DisplayController
    val inputController: InputController
    val mediaController: MediaController
    val restClient: RestClient

    init {
        CLIENT = this
        println("VR client startup...")

        rendererController = RendererController(this)
        displayController = DisplayController(this)
        inputController = InputController(this)
        mediaController = MediaController(this)
        vrController = VrController(this)

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

        mediaController.loadMedia()

        display.requestAnimationFrame({ time -> render() })

    }

    fun render(): Unit {
        var timeMillis = Date().getTime()

        if (renderTime != 0.0) {
            renderTimeDelta = timeMillis / 1000.0 - renderTime
        }
        renderTime = timeMillis / 1000.0

        inputController.render()
        vrController.render()
        rendererController.render()
        displayController.render(rendererController.scene, rendererController.camera)

        display.requestAnimationFrame({ time -> render() })
    }
}
