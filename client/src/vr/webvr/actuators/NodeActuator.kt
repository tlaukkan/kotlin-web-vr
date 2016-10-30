package vr.webvr.actuators

import lib.threejs.Object3D
import vr.network.model.Node
import vr.webvr.VirtualRealityController

abstract class NodeActuator(var controller: VirtualRealityController, var type: String) {

    abstract fun add(node: Node): Unit

    abstract fun update(node: Node): Unit

    abstract fun remove(node: Node): Unit

    fun updateObjectFromNode(obj: Object3D, node: Node) {
        obj.position.x = node.position.x
        obj.position.y = node.position.y
        obj.position.z = node.position.z
        obj.quaternion.x = node.orientation.x
        obj.quaternion.y = node.orientation.y
        obj.quaternion.z = node.orientation.z
        obj.quaternion.w = node.orientation.w
        obj.scale.x = node.scale.x
        obj.scale.y = node.scale.y
        obj.scale.z = node.scale.z
    }
}