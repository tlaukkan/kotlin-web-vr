package vr.webvr.actuators

import lib.threejs.*
import lib.threejs.Extra.BoxGeometry
import vr.network.model.Node
import vr.network.model.PrimitiveNode
import vr.util.dynamicCast
import vr.webvr.VirtualRealityController

class PrimitiveActuator(controller: VirtualRealityController) : NodeActuator(controller, "PrimitiveNode") {

    override fun add(node: Node) {
        val typedNode: PrimitiveNode = dynamicCast(node)
        if ("box".equals(typedNode.shape)) {

            controller.mediaController.loadTexture(typedNode.texture , { path, texture ->
                var geometry = BoxGeometry(1, 1, 1)

                var material = MeshBasicMaterial(object {})
                material.map = texture

                val obj = Mesh(geometry, material)
                add(node, obj)
            })

        } else {
            println("Unknown primitive shape: ${typedNode.shape}")
            return
        }
    }

}