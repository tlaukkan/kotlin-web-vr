package vr.webvr.actuators

import lib.threejs.AmbientLight
import vr.network.model.LightFieldNode
import vr.network.model.Node
import vr.util.dynamicCast
import vr.webvr.VirtualRealityController

class LightFieldActuator(controller: VirtualRealityController) : NodeActuator(controller, "LightFieldNode") {

    override fun add(node: Node) {
        val typedNode: LightFieldNode = dynamicCast(node)
        val obj = AmbientLight(typedNode.color, typedNode.intensity)
        obj.name = node.id
        updateObjectFromNode(obj, node)
        controller.scene.add(obj)
    }

    override fun update(node: Node) {
        val obj = controller.scene.getObjectByName(node.id)
        if (obj != null) {
            updateObjectFromNode(obj, node)
        }
    }

    override fun remove(node: Node) {
        val obj = controller.scene.getObjectByName(node.id)
        if (obj != null) {
            controller.scene.remove(obj)
        }
    }
}