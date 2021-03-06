package vr.webvr.actuators

import vr.CLIENT
import lib.threejs.Object3D
import lib.threejs.Texture
import lib.threejs.Vector3
import vr.network.model.Node
import vr.util.toJson
import vr.webvr.VrController

abstract class NodeActuator(var controller: VrController, var type: String) {

    abstract fun construct(node: Node, onConstructed:(obj: Object3D?) -> Unit): Unit

    abstract fun add(node: Node): Unit

    protected fun add(node: Node, obj: Object3D) {
        obj.name = node.url
        updateObjectFromNode(obj, node)

        updateInterpolator(node, null)

        add(node.parentUrl, obj)
        //controller.scene.add(obj)
    }

    protected fun add(parentUrl: String?, obj: Object3D) {
        if (controller.orphans.containsKey(obj.name)) {
            val orphanList = controller.orphans[obj.name]!!
            for (orphan in orphanList) {
                obj.add(orphan)
            }
            orphanList.clear()
            controller.orphans.remove(obj.name)
        }

        if (parentUrl != null && parentUrl!!.size != 0) {
            val parentObject = controller.scene.getObjectByName(parentUrl)
            if (parentObject != null) {
                parentObject.add(obj)
                println("Added ${obj.name} to parent $parentUrl.")
            } else {
                if (!controller.orphans.containsKey(parentUrl)) {
                    controller.orphans[parentUrl] = mutableListOf()
                }
                controller.orphans[parentUrl]!!.add(obj)
                println("Added orphan ${obj.name} as parent $parentUrl was not found.")
            }
        } else {
            CLIENT!!.vrController!!.roomGroup.add(obj)
        }
    }

    open fun update(node: Node, oldNode: Node) {
        updateInterpolator(node, oldNode)

        /*
        val obj = controller.scene.getObjectByName(node.url)
        if (obj != null) {
            updateObjectFromNode(obj, node)
        }
        */
    }

    private fun updateInterpolator(node: Node, oldNode: Node?) {
        if (!controller.nodeInterpolators.containsKey(node.url)) {
            val interpolator = NodeInterpolator(node.url)
            controller.nodeInterpolators.put(node.url, interpolator)
            if (oldNode != null) {
                interpolator.initialize(CLIENT!!.renderTime, oldNode)
                interpolator.update(CLIENT!!.renderTime, node)
                //println("Added node interpolator for old node: " + node.url)
                /*val oldPosition = Vector3()
                CLIENT!!.vrController.getNodePositionToLocalCoordinates(oldNode, oldPosition)
                val newPosition = Vector3()
                CLIENT!!.vrController.getNodePositionToLocalCoordinates(node, newPosition)
                println(newPosition.distanceTo(oldPosition))
                println(toJson(node))
                println(toJson(oldNode))*/
            } else {
                interpolator.initialize(CLIENT!!.renderTime, node)
                //println("Added node interpolator for new node: " + node.url)
            }
        } else {
            controller.nodeInterpolators[node.url]!!.update(CLIENT!!.renderTime, node)
        }
    }

    open fun remove(node: Node) {
        val obj = controller.scene.getObjectByName(node.url)
        if (obj != null) {
            controller.roomGroup.remove(obj)
        }
    }


    fun updateObjectFromNode(obj: Object3D, node: Node) {
        CLIENT!!.vrController!!.getNodePositionToLocalCoordinates(node, obj.position)
        obj.quaternion.x = node.orientation.x
        obj.quaternion.y = node.orientation.y
        obj.quaternion.z = node.orientation.z
        obj.quaternion.w = node.orientation.w
        obj.scale.x = node.scale.x
        obj.scale.y = node.scale.y
        obj.scale.z = node.scale.z
    }

}