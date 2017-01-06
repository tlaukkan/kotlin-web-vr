package vr.webvr.actuators

import lib.threejs.*
import lib.threejs.Extra.BoxGeometry
import lib.threejs.Extra.SphereGeometry
import vr.network.model.LightNode
import vr.network.model.Node
import vr.network.model.PrimitiveNode
import vr.util.dynamicCast
import vr.webvr.VirtualRealityController

class LightActuator(controller: VirtualRealityController) : NodeActuator(controller, "LightNode") {

    override fun construct(node: Node, onConstructed: (Object3D?) -> Unit) {
        val typedNode: LightNode = dynamicCast(node)

        val geometry: Geometry?

        geometry = SphereGeometry(0.5, 50, 50, 0.0, Math.PI * 2, 0.0, Math.PI * 2)

        val material = MeshBasicMaterial()
        material.transparent = true
        material.color = Color(typedNode.color)
        material.opacity = node.opacity
        if (node.opacity < 1.0) {
            material.transparent = true
        }

        val obj = Mesh(geometry, material)
        obj.castShadow = false
        obj.receiveShadow = false

        val light = PointLight(typedNode.color, typedNode.intensity, typedNode.distance, typedNode.decay)
        light.castShadow = true

        //light.shadowMapWidth = 2048
        //light.shadowMapHeight = 2048

        light.shadow.camera.near = 0.1
        light.shadow.camera.far = typedNode.distance / 2
        light.shadow.bias = -0.005


        /*val d = 10
        light.shadowCameraLeft = -d
        light.shadowCameraRight = d
        light.shadowCameraTop = d
        light.shadowCameraBottom = -d*/

        /*val d = 10
        light.shadowCameraLeft = -d
        light.shadowCameraRight = d
        light.shadowCameraTop = d
        light.shadowCameraBottom = -d*/

        //light.shadowMapWidth = 8192
        //light.shadowMapHeight = 8192
        //light.shadow.bias = 0.000015

        obj.add(light)

        onConstructed(obj)

    }

    override fun add(node: Node) {
        construct(node, { obj ->
            if (obj != null) {
                add(node, obj!!)
            }
        })
    }

}