package vr.webvr

import vr.CLIENT
import lib.threejs.Group
import lib.threejs.Object3D
import lib.threejs.Vector3
import vr.client.VrClient
import vr.network.NetworkClient
import vr.network.model.Node
import vr.util.dynamicCast
import vr.util.toJson
import vr.webvr.actuators.*

class VrController(vrClient: VrClient) {

    var scene = vrClient.displayController.scene
    val nodeActuators: MutableMap<String, NodeActuator> = mutableMapOf()
    val nodeInterpolators: MutableMap<String, NodeInterpolator> = mutableMapOf()
    val nodes: MutableMap<String, Node> = mutableMapOf()
    val nodeTypes: MutableMap<String, String> = mutableMapOf()
    val orphans: MutableMap<String, MutableList<Object3D>> = mutableMapOf()

    var networkClient: NetworkClient? = null
    var linkedServerCellUrl: String? = null
    var neighbours: MutableMap<String, Vector3> = mutableMapOf()

    var roomGroup = Group()

    fun addNodeActuator(nodeActuator: NodeActuator) {
        nodeActuators[nodeActuator.type] = nodeActuator
    }

    init {
        roomGroup.position.x = 0.0
        roomGroup.position.y = 0.0
        roomGroup.position.z = 0.0
        scene.add(roomGroup)
        addNodeActuator(LightFieldActuator(this))
        addNodeActuator(LightActuator(this))
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

            if (node.removed) {
                if (nodes.containsKey(node.url)) {
                    nodeActuator.remove(node)
                    nodes.remove(node.url)
                    nodeTypes.remove(node.url)
                    println("Removed ${node.url} $type")
                }
            } else {
                nodeTypes.put(node.url, type)
                if (nodes.containsKey(node.url)) {
                    val oldNode = nodes[node.url]!!
                    nodes[node.url] = node
                    nodeActuator.update(node, oldNode)
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

    fun getNodePositionToLocalCoordinates(node: Node, position: Vector3) : Unit {
        val cellUri = node.url.substring(0, node.url.lastIndexOf('/'))

        position.x = node.position.x
        position.y = node.position.y
        position.z = node.position.z

        if (neighbours.containsKey(cellUri) && node.parentUrl == null) {
            val neighbourVector = neighbours[cellUri]!!
            position.x += neighbourVector.x
            position.y += neighbourVector.y
            position.z += neighbourVector.z
        }
    }

    fun setNodePositionFromWorldCoordinates(node: Node, position: Vector3) : Unit {
        val cellUri = node.url.substring(0, node.url.lastIndexOf('/'))

        val nodePosition: Vector3 = position.clone()

        nodePosition.x -= roomGroup.position.x
        nodePosition.y -= roomGroup.position.y
        nodePosition.z -= roomGroup.position.z

        if (neighbours.containsKey(cellUri) && node.parentUrl == null) {
            val neighbourVector = neighbours[cellUri]!!
            nodePosition.x -= neighbourVector.x
            nodePosition.y -= neighbourVector.y
            nodePosition.z -= neighbourVector.z
        }

        node.position.x = nodePosition.x
        node.position.y = nodePosition.y
        node.position.z = nodePosition.z
    }

    fun render() {
        val nodeUrlsToRemoveFromInterpolators: MutableList<String> = mutableListOf()
        for (interpolator in nodeInterpolators.values) {
            val obj = scene.getObjectByName(interpolator.nodeUrl)
            if (obj == null) {
                nodeUrlsToRemoveFromInterpolators.add(interpolator.nodeUrl)
                continue
            }
            if (!interpolator.interpolate(CLIENT!!.renderTime, CLIENT!!.renderTimeDelta, obj)) {
                nodeUrlsToRemoveFromInterpolators.add(interpolator.nodeUrl)
            }
        }
        for (interpolatorKeyToRemove in nodeUrlsToRemoveFromInterpolators) {
            nodeInterpolators.remove(interpolatorKeyToRemove)
            println("Removed interpolator for node: " + interpolatorKeyToRemove)
        }
    }

}