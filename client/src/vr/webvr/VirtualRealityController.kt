package vr.webvr

import vr.network.model.Node
import vr.util.dynamicCast
import vr.webvr.actuators.NodeActuator
import vr.webvr.actuators.LightFieldActuator

class VirtualRealityController(var displayController: DisplayController) {

    var scene = displayController.scene
    val nodeActuators: MutableMap<String, NodeActuator> = mutableMapOf()
    val nodes: MutableMap<String, Node> = mutableMapOf()

    fun addNodeActuator(nodeActuator: NodeActuator) {
        nodeActuators[nodeActuator.type] = nodeActuator
    }

    init {
        addNodeActuator(LightFieldActuator(this))
    }

    fun onReceive(type: String, value: Any) {
        if (nodeActuators.containsKey(type)) {
            val nodeActuator = nodeActuators[type]!!
            val node : Node = dynamicCast(value)
            if (node.removed) {
                if (nodes.containsKey(node.id)) {
                    nodeActuator.remove(node)
                    nodes.remove(node.id)
                    println("Removed ${node.id} $type")
                }
            } else {
                if (nodes.containsKey(node.id)) {
                    nodes[node.id] = node
                    nodeActuator.update(node)
                } else {
                    nodes[node.id] = node
                    nodeActuator.add(node)
                    println("Added ${node.id} $type")
                }
            }
        } else {
            println("No activator defined for $type")
        }
    }

}