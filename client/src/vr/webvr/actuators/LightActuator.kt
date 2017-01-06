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

        geometry = SphereGeometry(1.0, 50, 50, 0.0, Math.PI * 2, 0.0, Math.PI * 2)
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