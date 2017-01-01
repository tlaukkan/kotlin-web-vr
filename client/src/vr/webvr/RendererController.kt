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
        camera = PerspectiveCamera(75.0, window.innerWidth / window.innerHeight, 0.1, 512.0)
        renderer = WebGLRenderer( object {var antialias = true})
        renderer.setSize(window.innerWidth, window.innerHeight)
        renderer.setPixelRatio(window.devicePixelRatio)
        renderer.sortObjects = false
        renderer.setClearColor(0xefd1b5)

        val renderer2: dynamic = renderer
        renderer2.physicallyCorrectLights = true
        renderer2.gammaInput = true
        renderer2.gammaOutput = true
        renderer2.shadowMap.enabled = true
        renderer2.shadowMapEnabled = true
        renderer2.shadowMapSoft = true


        renderer2.shadowCameraNear = 3
        renderer2.shadowCameraFar = camera.far
        renderer2.shadowCameraFov = 40

        renderer2.shadowMapBias = 0.0
        renderer2.shadowMapDarkness = 0.5
        renderer2.shadowMapWidth = 2048
        renderer2.shadowMapHeight = 2048


        //renderer2.toneMapping = THREE.ReinhardToneMapping;


        document.body!!.appendChild(renderer.domElement)

        scene.fog = FogExp2(Color(0xefd1b5), 0.05)

        //val sky = Sky()
        //scene.add(sky.mesh)

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