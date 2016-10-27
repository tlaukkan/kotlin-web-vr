import webvr.DisplayController
import webvr.GraphicsController
import webvr.VirtualRealityController
import kotlin.browser.window

fun main(args: Array<String>) {
    println("VR client startup...")



    val virtualRealityController = VirtualRealityController()

    virtualRealityController.startup({
        val graphicsController = GraphicsController()
        val displayController = DisplayController(virtualRealityController, graphicsController)

        fun render(time: Double) {
            graphicsController.render(time)
            displayController.render(graphicsController.scene, graphicsController.camera)
            window.requestAnimationFrame(::render)
        }
        render(1.0)

    }, { error ->
        println("Startup interrupted: " + error)
    })
}
