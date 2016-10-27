import threejs.*
import threejs.Extra.BoxGeometry
import kotlin.browser.document
import kotlin.browser.window

/**
 * Created by tlaukkan on 10/26/2016.
 */
class GraphicsController {

    val scene: Scene
    val renderer: WebGLRenderer
    val camera: PerspectiveCamera
    val cube: Mesh
    init {
        scene = Scene()
        camera = PerspectiveCamera(75.0, window.innerWidth / window.innerHeight, 0.1, 1000.0)
        renderer = WebGLRenderer()
        renderer.setSize(window.innerWidth, window.innerHeight)
        renderer.setPixelRatio(window.devicePixelRatio)
        //renderer.sortObjects = false
        renderer.setClearColor(0x101010)


        document.body!!.appendChild(renderer.domElement)

        var geometry = BoxGeometry(1, 1, 1)

        var material = MeshBasicMaterial(object {
            var color: Int = 0xffff00
        })

        cube = Mesh(geometry, material)
        scene.add(cube)

        camera.position.z = 5.0
    }

    fun render(time: Double) {

        cube.rotation.x += 0.1
        cube.rotation.y += 0.1

        renderer.render(scene, camera)
    }
}