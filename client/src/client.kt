import threejs.*
import threejs.Extra.BoxGeometry
import kotlin.browser.document
import kotlin.browser.window

fun main(args: Array<String>) {
    println("VR client startup...")

    var scene = Scene()

    var camera = PerspectiveCamera(75.0, window.innerWidth / window.innerHeight, 0.1, 1000.0)

    var renderer = WebGLRenderer()
    renderer.setSize(window.innerWidth, window.innerHeight)
    document.body!!.appendChild(renderer.domElement)

    var geometry = BoxGeometry(1, 1, 1)
    var material = MeshBasicMaterial(mapOf("color" to 0x00ff00))
    var cube = Mesh(geometry, material)
    scene.add(cube)

    camera.position.z = 5.0

    fun render(time: Double) {
        window.requestAnimationFrame(::render)

        cube.rotation.x += 0.1
        cube.rotation.y += 0.1

        renderer.render(scene, camera)
    }

    render(1.0)
}

