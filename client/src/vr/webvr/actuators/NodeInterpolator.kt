package vr.webvr.actuators

import lib.threejs.Object3D
import lib.threejs.Quaternion
import lib.threejs.Vector3
import vr.network.model.DataQuaternion
import vr.network.model.DataVector3
import vr.network.model.Node

/**
 * Created by tlaukkan on 12/31/2016.
 */
class NodeInterpolator(val nodeUrl: String) {

    var initialized = false

    val targetPosition: Vector3 = Vector3(0.0, 0.0, 0.0)
    val targetOrientation: Quaternion = Quaternion(0.0, 0.0, 0.0, 1.0)
    val targetScale: Vector3 = Vector3(1.0, 1.0, 1.0)

    val position: Vector3 = Vector3(0.0, 0.0, 0.0)
    val intermediatePosition: Vector3 = Vector3(0.0, 0.0, 0.0)
    val orientation: Quaternion = Quaternion(0.0, 0.0, 0.0, 1.0)
    val scale: Vector3 = Vector3(1.0, 1.0, 1.0)
    var lastUpdateTime = 0.0
    var timeWindow = 0.3

    fun updateTarget(time: Double, timeDelta: Double, node: Node) {
        println("Updated node: $nodeUrl($time / $timeDelta)")

        targetPosition.x = node.position.x
        targetPosition.y = node.position.y
        targetPosition.z = node.position.z
        targetOrientation.x = node.orientation.x
        targetOrientation.y = node.orientation.y
        targetOrientation.z = node.orientation.z
        targetOrientation.w = node.orientation.w
        targetScale.x = node.scale.x
        targetScale.y = node.scale.y
        targetScale.z = node.scale.z

        if (!initialized) {
            initialized = true
            position.x = node.position.x
            position.y = node.position.y
            position.z = node.position.z
            intermediatePosition.x = node.position.x
            intermediatePosition.y = node.position.y
            intermediatePosition.z = node.position.z
            orientation.x = node.orientation.x
            orientation.y = node.orientation.y
            orientation.z = node.orientation.z
            orientation.w = node.orientation.w
            scale.x = node.scale.x
            scale.y = node.scale.y
            scale.z = node.scale.z
        }

        if (lastUpdateTime != 0.0) {
            timeWindow = (9 * timeWindow + (time - lastUpdateTime)) / 10
        }
        lastUpdateTime = time
    }

    /**
     *
     */
    fun interpolate(time: Double, timeDelta: Double, obj: Object3D) : Boolean {

        //println("Interpolated node: $nodeUrl($time / $timeDelta)")

        position.add(getStep(position, intermediatePosition, timeDelta, timeWindow * 2.0))

        intermediatePosition.add(getStep(intermediatePosition, targetPosition, timeDelta, timeWindow))

        //val newPosition = targetPosition

        //position.x = newPosition.x
        //position.y = newPosition.y
        //position.z = newPosition.z

        obj.position.x = position.x
        obj.position.y = position.y
        obj.position.z = position.z
        obj.quaternion.x = targetOrientation.x
        obj.quaternion.y = targetOrientation.y
        obj.quaternion.z = targetOrientation.z
        obj.quaternion.w = targetOrientation.w
        obj.scale.x = targetScale.x
        obj.scale.y = targetScale.y
        obj.scale.z = targetScale.z

        return time - lastUpdateTime < 1000
    }

    private fun getStep(source: Vector3, target: Vector3, timeDelta: Double, timeWindow: Double): Vector3 {
        val positionDelta = Vector3()
        positionDelta.subVectors(target, source)

        val positionDeltaLength = positionDelta.length()
        val translationVelocity = positionDeltaLength / timeWindow
        positionDelta.multiplyScalar(translationVelocity * timeDelta / positionDeltaLength)
        return positionDelta
    }


}