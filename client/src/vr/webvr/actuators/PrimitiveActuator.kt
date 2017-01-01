package vr.webvr.actuators

import lib.threejs.*
import lib.threejs.Extra.BoxGeometry
import vr.network.model.Node
import vr.network.model.PrimitiveNode
import vr.util.dynamicCast
import vr.webvr.VirtualRealityController

class PrimitiveActuator(controller: VirtualRealityController) : NodeActuator(controller, "PrimitiveNode") {

    override fun construct(node: Node, onConstructed: (Object3D?) -> Unit) {
        val typedNode: PrimitiveNode = dynamicCast(node)
        if ("box".equals(typedNode.shape)) {
            controller.mediaController.loadTexture(typedNode.texture , { path, texture ->
                var geometry = BoxGeometry(1, 1, 1)

                var material = MeshStandardMaterial(object {})
                material.map = texture
                material.color = Color(0xffffff)
                material.lights = true

                val obj = Mesh(geometry, material)

                obj.castShadow = true
                obj.receiveShadow = true

                onConstructed(obj)
            })
        } else {
            println("Unknown primitive shape: ${typedNode.shape}")
            onConstructed(null)
        }
    }

    override fun add(node: Node) {
        construct(node, { obj ->
            if (obj != null) {
                add(node, obj!!)
            }
        })
    }

}