package vr.webvr

import lib.threejs.Object3D
import lib.threejs.Vector3
import renderTime
import renderTimeDelta
import vr.network.model.Node
import vr.util.dynamicCast
import vr.webvr.actuators.NodeActuator
import vr.webvr.actuators.LightFieldActuator
import vr.webvr.actuators.NodeInterpolator
import vr.webvr.actuators.PrimitiveActuator

class VirtualRealityController(var displayController: DisplayController, var mediaController: MediaController) {

    var scene = displayController.scene
    val nodeActuators: MutableMap<String, NodeActuator> = mutableMapOf()
    val nodeInterpolators: MutableMap<String, NodeInterpolator> = mutableMapOf()
    val nodes: MutableMap<String, Node> = mutableMapOf()

    // Orphaned3D objects
    val orphans: MutableMap<String, MutableList<Object3D>> = mutableMapOf()

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

    fun update() {
        val nodeUrlsToRemoveFromInterpolators: MutableList<String> = mutableListOf()
        for (interpolator in nodeInterpolators.values) {
            val obj = scene.getObjectByName(interpolator.nodeUrl)
            if (obj == null) {
                nodeUrlsToRemoveFromInterpolators.add(interpolator.nodeUrl)
                continue
            }
            if (!interpolator.interpolate(renderTime, renderTimeDelta, obj)) {
                nodeUrlsToRemoveFromInterpolators.add(interpolator.nodeUrl)
            }
        }
        for (interpolatorKeyToRemove in nodeUrlsToRemoveFromInterpolators) {
            nodeInterpolators.remove(interpolatorKeyToRemove)
            println("Removed interpolator for node: " + interpolatorKeyToRemove)
        }
    }

}