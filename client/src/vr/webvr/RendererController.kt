package vr.webvr

import lib.threejs.*
import lib.threejs.Extra.BoxGeometry
import kotlin.browser.document
import kotlin.browser.window

/**
 * Created by tlaukkan on 10/26/2016.
 */
class RendererController {

    val scene: Scene
    val renderer: WebGLRenderer
    val camera: PerspectiveCamera
    //val cube: Mesh

    init {
        scene = Scene()
        camera = PerspectiveCamera(75.0, window.innerWidth / window.innerHeight, 0.1, 1000.0)
        renderer = WebGLRenderer( object {var antialias = true})
        renderer.setSize(window.innerWidth, window.innerHeight)
        renderer.setPixelRatio(window.devicePixelRatio)
        renderer.sortObjects = false
        renderer.setClearColor(0x101010)


        document.body!!.appendChild(renderer.domElement)

        //var geometry = BoxGeometry(1, 1, 1)

        //var material = MeshBasicMaterial(object {
        //    var color: Int = 0xffff00
        //})

        //cube = Mesh(geometry, material)
        //cube.scale.x = 0.1
        //cube.scale.y = 0.1
        //cube.scale.z = 0.1
        //scene.add(cube)

    }

    fun render() {

        //cube.rotation.x += 0.1
        //cube.rotation.y += 0.1

        renderer.render(scene, camera)
    }
}