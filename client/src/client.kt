
fun main(args: Array<String>) {
    println("VR client startup...")



    val virtualRealityController = VirtualRealityController()

    var graphicsController: GraphicsController

    virtualRealityController.startup({
        graphicsController = GraphicsController()
    }, { error ->
        println("Startup interrupted: " + error)
    })
}
