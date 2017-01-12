package vr.webvr

import lib.threejs.*
import lib.threejs.Extra.BoxGeometry
import kotlin.browser.document
import kotlin.browser.window

/**
 * Created by tlaukkan on 10/26/2016.
 */
class RendererController(val vrClient: VrClient) {

    val scene: Scene
    val renderer: WebGLRenderer
    val camera: PerspectiveCamera

    init {
        scene = Scene()
        camera = PerspectiveCamera(75.0, window.innerWidth / window.innerHeight, 0.1, 512.0)
        renderer = WebGLRenderer( object {var antialias = true})
        renderer.setSize(window.innerWidth, window.innerHeight)
        renderer.setPixelRatio(window.devicePixelRatio)
        renderer.sortObjects = false
        renderer.setClearColor(0x000000)

        renderer.physicallyCorrectLights = true
        renderer.gammaInput = true
        renderer.gammaOutput = true

        renderer.shadowMapEnabled = true
        //renderer.shadowMap.Enabled = true
        renderer.shadowMap.type = PCFSoftShadowMap

        document.body!!.appendChild(renderer.domElement)
        scene.fog = FogExp2(Color(0x000000), 0.05)
    }

    fun render() {
        renderer.render(scene, camera)
    }
}