package vr.webvr.actuators

import lib.threejs.*
import lib.threejs.Extra.BoxGeometry
import lib.threejs.Extra.SphereGeometry
import vr.network.model.LightFieldNode
import vr.network.model.Node
import vr.network.model.PrimitiveNode
import vr.util.dynamicCast
import vr.webvr.VirtualRealityController

class LightFieldActuator(controller: VirtualRealityController) : NodeActuator(controller, "LightFieldNode") {

    override fun construct(node: Node, onConstructed: (Object3D?) -> Unit) {
        val typedNode: LightFieldNode = dynamicCast(node)

        val geometry = SphereGeometry(1.0, 50, 50, 0.0, Math.PI * 2, 0.0, Math.PI * 2)

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

        val light: Object3D
        if (typedNode.direction == null) {
            light = AmbientLight(typedNode.color, typedNode.intensity)
        } else {
            light = DirectionalLight(typedNode.color, typedNode.intensity)
            light.castShadow = true
            light.position.x = typedNode.direction!!.x
            light.position.y = typedNode.direction!!.y
            light.position.z = typedNode.direction!!.z
            val d = 10
            light.shadowCameraLeft = -d
            light.shadowCameraRight = d
            light.shadowCameraTop = d
            light.shadowCameraBottom = -d
            light.shadowMapWidth = 8192
            light.shadowMapHeight = 8192
            light.shadow.bias = 0.000015
        }
//        light.name = node.url
//        light.updateMatrix()
//        light.updateMatrixWorld()

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