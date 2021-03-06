package vr.webvr.actuators

import vr.CLIENT
import lib.threejs.*
import lib.threejs.Extra.BoxGeometry
import lib.threejs.Extra.SphereGeometry
import vr.client.VrClient
import vr.network.model.Node
import vr.network.model.PrimitiveNode
import vr.util.dynamicCast
import vr.webvr.VrController

class PrimitiveActuator(controller: VrController) : NodeActuator(controller, "PrimitiveNode") {

    override fun construct(node: Node, onConstructed: (Object3D?) -> Unit) {
        val typedNode: PrimitiveNode = dynamicCast(node)

        CLIENT!!.mediaController.loadTexture(typedNode.texture , { path, texture ->
            val geometry: Geometry?

            if ("box".equals(typedNode.shape)) {
                    geometry = BoxGeometry(1.0, 1.0, 1.0)
            } else if ("sphere".equals(typedNode.shape)) {
                geometry = SphereGeometry(0.5, 40, 40, 0.0, Math.PI * 2, 0.0, Math.PI * 2)
            } else {
                geometry = null
            }

            if (geometry != null) {
                /*
                var material = MeshPhongMaterial(object {})
                material.map = texture
                material.opacity = node.opacity
                if (node.opacity < 1.0) {
                    material.transparent = true
                }
                */
                var material = CLIENT!!.materialFactory.getMaterial(typedNode.texture, texture, node.opacity)

                val obj = Mesh(geometry, material)
                if (!("sphere".equals(typedNode.shape)) ) {
                    //obj.castShadow = true
                } else {
                    //val shadowObject = Mesh(SphereGeometry(0.48, 40, 40, 0.0, Math.PI * 2, 0.0, Math.PI * 2), MeshBasicMaterial(object {}))
                    //shadowObject.castShadow = true
                    //obj.add(shadowObject)
                }
                //obj.receiveShadow = true

                onConstructed(obj)
            } else {
                println("Unknown primitive shape: ${typedNode.shape}")
                onConstructed(null)
            }

        })

    }

    override fun add(node: Node) {
        construct(node, { obj ->
            if (obj != null) {
                add(node, obj!!)
            }
        })
    }

}