package vr.webvr

import lib.threejs.Vector3
import vr.network.model.Node
import vr.util.dynamicCast
import vr.webvr.actuators.NodeActuator
import vr.webvr.actuators.LightFieldActuator
import vr.webvr.actuators.PrimitiveActuator

class VirtualRealityController(var displayController: DisplayController, var mediaController: MediaController) {

    var scene = displayController.scene
    val nodeActuators: MutableMap<String, NodeActuator> = mutableMapOf()
    val nodes: MutableMap<String, Node> = mutableMapOf()
    var neighbours: MutableMap<String, Vector3> = mutableMapOf()


    fun addNodeActuator(nodeActuator: NodeActuator) {
        nodeActuators[nodeActuator.type] = nodeActuator
    }

    init {
        addNodeActuator(LightFieldActuator(this))
        addNodeActuator(PrimitiveActuator(this))
    }

    fun onReceive(type: String, value: Any) {
        if (nodeActuators.containsKey(type)) {
            val nodeActuator = nodeActuators[type]!!
            val node : Node = dynamicCast(value)

            val cellUri: String
            if (node.url.contains('/')) {
                cellUri = node.url.substring(0, node.url.lastIndexOf('/'))
            } else {
                return
            }

            if (neighbours.containsKey(cellUri)) {
                val neighbourVector = neighbours[cellUri]!!
                node.position.x += neighbourVector.x
                node.position.y += neighbourVector.y
                node.position.z += neighbourVector.z
            }

            if (node.removed) {
                if (nodes.containsKey(node.url)) {
                    nodeActuator.remove(node)
                    nodes.remove(node.url)
                    println("Removed ${node.url} $type")
                }
            } else {
                if (nodes.containsKey(node.url)) {
                    nodes[node.url] = node
                    nodeActuator.update(node)
                } else {
                    nodes[node.url] = node
                    nodeActuator.add(node)
                    println("Added ${node.url} $type")
                }
            }
        } else {
            println("No activator defined for $type")
        }
    }

}