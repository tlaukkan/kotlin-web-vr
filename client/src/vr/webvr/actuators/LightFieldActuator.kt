package vr.webvr.actuators

import lib.threejs.AmbientLight
import lib.threejs.DirectionalLight
import lib.threejs.Object3D
import vr.network.model.LightFieldNode
import vr.network.model.Node
import vr.util.dynamicCast
import vr.webvr.VirtualRealityController

class LightFieldActuator(controller: VirtualRealityController) : NodeActuator(controller, "LightFieldNode") {

    override fun add(node: Node) {
        val typedNode: LightFieldNode = dynamicCast(node)
        val obj: Object3D
        if (typedNode.direction == null) {
            obj = AmbientLight(typedNode.color, typedNode.intensity)
        } else {
            obj = DirectionalLight(typedNode.color, typedNode.intensity)
            obj.position.x = typedNode.direction!!.x
            obj.position.y = typedNode.direction!!.y
            obj.position.z = typedNode.direction!!.z
        }
        obj.name = node.url
        obj.updateMatrix()
        obj.updateMatrixWorld()
//        add(node, obj)
        //controller.scene.add(obj)
        add(node.parentUrl, obj)
    }

}