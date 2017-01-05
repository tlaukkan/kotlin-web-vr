package vr.webvr.actuators

import lib.threejs.Object3D
import lib.threejs.Texture
import renderTime
import renderTimeDelta
import virtualRealityController
import vr.network.model.Node
import vr.webvr.VirtualRealityController

abstract class NodeActuator(var controller: VirtualRealityController, var type: String) {

    abstract fun construct(node: Node, onConstructed:(obj: Object3D?) -> Unit): Unit

    abstract fun add(node: Node): Unit

    protected fun add(node: Node, obj: Object3D) {
        obj.name = node.url
        updateObjectFromNode(obj, node)

        interpolate(node)

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
            controller.scene.add(obj)
        }
    }

    open fun update(node: Node) {
        interpolate(node)

        /*
        val obj = controller.scene.getObjectByName(node.url)
        if (obj != null) {
            updateObjectFromNode(obj, node)
        }
        */
    }

    private fun interpolate(node: Node) {
        if (!controller.nodeInterpolators.containsKey(node.url)) {
            controller.nodeInterpolators.put(node.url, NodeInterpolator(node.url))
            println("Added node interpolator for node: " + node.url)
        }
        controller.nodeInterpolators[node.url]!!.updateTarget(renderTime, renderTimeDelta, node)
    }

    open fun remove(node: Node) {
        val obj = controller.scene.getObjectByName(node.url)
        if (obj != null) {
            controller.scene.remove(obj)
        }
    }


    fun updateObjectFromNode(obj: Object3D, node: Node) {
        virtualRealityController!!.getNodePosition(node, obj.position)
        obj.quaternion.x = node.orientation.x
        obj.quaternion.y = node.orientation.y
        obj.quaternion.z = node.orientation.z
        obj.quaternion.w = node.orientation.w
        obj.scale.x = node.scale.x
        obj.scale.y = node.scale.y
        obj.scale.z = node.scale.z
    }

}