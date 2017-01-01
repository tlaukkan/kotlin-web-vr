package vr.webvr.actuators

import lib.threejs.*
import lib.threejs.Extra.BoxGeometry
import vr.network.model.LightFieldNode
import vr.network.model.Node
import vr.network.model.PrimitiveNode
import vr.util.dynamicCast
import vr.webvr.VirtualRealityController

class LightFieldActuator(controller: VirtualRealityController) : NodeActuator(controller, "LightFieldNode") {

    override fun construct(node: Node, onConstructed: (Object3D?) -> Unit) {
        val typedNode: LightFieldNode = dynamicCast(node)
        val obj: Object3D
        if (typedNode.direction == null) {
            obj = AmbientLight(typedNode.color, typedNode.intensity)
        } else {
            obj = DirectionalLight(typedNode.color, typedNode.intensity)
            obj.castShadow = true
            obj.position.x = typedNode.direction!!.x
            obj.position.y = typedNode.direction!!.y
            obj.position.z = typedNode.direction!!.z
            val d = 10
            obj.shadowCameraLeft = -d
            obj.shadowCameraRight = d
            obj.shadowCameraTop = d
            obj.shadowCameraBottom = -d
            obj.shadowMapWidth = 8192
            obj.shadowMapHeight = 8192
            obj.shadow.bias = 0.000015

        }
        obj.name = node.url
        obj.updateMatrix()
        obj.updateMatrixWorld()

        onConstructed(obj)
    }

    override fun add(node: Node) {
        construct(node, { obj ->
            if (obj != null) {
                add(node.parentUrl, obj!!)
            }
        })
    }

}