package vr.webvr.actuators

import lib.threejs.Object3D
import vr.network.model.Node
import vr.webvr.VirtualRealityController

abstract class NodeActuator(var controller: VirtualRealityController, var type: String) {

    abstract fun add(node: Node): Unit

    private val orphans: MutableMap<String, MutableList<Object3D>> = mutableMapOf()

    protected fun add(node: Node, obj: Object3D) {
        obj.name = node.url
        updateObjectFromNode(obj, node)
        add(node.parentUrl, obj)
        //controller.scene.add(obj)
    }

    protected fun add(parentUrl: String?, obj: Object3D) {
        if (orphans.containsKey(obj.name)) {
            val orphanList = orphans[obj.name]!!
            for (orphan in orphanList) {
                obj.add(orphan)
            }
            orphanList.clear()
            orphans.remove(obj.name)
        }

        if (parentUrl != null && parentUrl!!.size != 0) {
            val parentObject = controller.scene.getObjectByName(parentUrl)
            if (parentObject != null) {
                parentObject.add(obj)
                println("Added ${obj.name} to parent $parentUrl.")
            } else {
                if (!orphans.containsKey(parentUrl)) {
                    orphans[parentUrl] = mutableListOf()
                }
                orphans[parentUrl]!!.add(obj)
                println("Added orphan ${obj.name} as parent $parentUrl was not found.")
            }
        } else {
            controller.scene.add(obj)
        }
    }

    open fun update(node: Node) {
        val obj = controller.scene.getObjectByName(node.url)
        if (obj != null) {
            updateObjectFromNode(obj, node)
        }
    }

    open fun remove(node: Node) {
        val obj = controller.scene.getObjectByName(node.url)
        if (obj != null) {
            controller.scene.remove(obj)
        }
    }

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