package vr.webvr.actuators

import vr.CLIENT
import lib.threejs.Object3D
import lib.threejs.Quaternion
import lib.threejs.Vector3
import vr.network.model.Node

/**
 * Created by tlaukkan on 12/31/2016.
 */
class NodeInterpolator(val nodeUrl: String) {

    var targetPosition: Vector3 = Vector3(0.0, 0.0, 0.0)
    val targetOrientation: Quaternion = Quaternion(0.0, 0.0, 0.0, 1.0)
    val targetScale: Vector3 = Vector3(1.0, 1.0, 1.0)

    val intermediatePosition: Vector3 = Vector3(0.0, 0.0, 0.0)
    val intermediateOrientation: Quaternion = Quaternion(0.0, 0.0, 0.0, 1.0)
    val intermediateScale: Vector3 = Vector3(1.0, 1.0, 1.0)

    val position: Vector3 = Vector3(0.0, 0.0, 0.0)
    val orientation: Quaternion = Quaternion(0.0, 0.0, 0.0, 1.0)
    val scale: Vector3 = Vector3(1.0, 1.0, 1.0)

    var lastUpdateTime = 0.0
    var timeWindow = 0.15

    fun initialize(time: Double, node: Node) {
        CLIENT!!.vrController!!.getNodePositionToLocalCoordinates(node, targetPosition)
        targetOrientation.x = node.orientation.x
        targetOrientation.y = node.orientation.y
        targetOrientation.z = node.orientation.z
        targetOrientation.w = node.orientation.w
        targetScale.x = node.scale.x
        targetScale.y = node.scale.y
        targetScale.z = node.scale.z

        position.x = targetPosition.x
        position.y = targetPosition.y
        position.z = targetPosition.z
        intermediatePosition.x = targetPosition.x
        intermediatePosition.y = targetPosition.y
        intermediatePosition.z = targetPosition.z

        orientation.x = node.orientation.x
        orientation.y = node.orientation.y
        orientation.z = node.orientation.z
        orientation.w = node.orientation.w

        intermediateOrientation.x = node.orientation.x
        intermediateOrientation.y = node.orientation.y
        intermediateOrientation.z = node.orientation.z
        intermediateOrientation.w = node.orientation.w

        scale.x = node.scale.x
        scale.y = node.scale.y
        scale.z = node.scale.z

        intermediateScale.x = node.scale.x
        intermediateScale.y = node.scale.y
        intermediateScale.z = node.scale.z

        lastUpdateTime = time
        //println("Initialized node: $nodeUrl ($time)")
    }

    fun update(time: Double, node: Node) {
        CLIENT!!.vrController!!.getNodePositionToLocalCoordinates(node, targetPosition)
        targetOrientation.x = node.orientation.x
        targetOrientation.y = node.orientation.y
        targetOrientation.z = node.orientation.z
        targetOrientation.w = node.orientation.w
        targetScale.x = node.scale.x
        targetScale.y = node.scale.y
        targetScale.z = node.scale.z

        //println("Updated node: $nodeUrl ($time)")

        timeWindow = (9 * timeWindow + (time - lastUpdateTime)) / 10
        lastUpdateTime = time
    }

    /**
     *
     */
    fun interpolate(time: Double, timeDelta: Double, obj: Object3D) : Boolean {

        val endOfInterpolation = time - lastUpdateTime >= timeWindow * 10

        if (endOfInterpolation) {
            obj.position.x = targetPosition.x
            obj.position.y = targetPosition.y
            obj.position.z = targetPosition.z
            obj.quaternion.x = targetOrientation.x
            obj.quaternion.y = targetOrientation.y
            obj.quaternion.z = targetOrientation.z
            obj.quaternion.w = targetOrientation.w
            obj.scale.x = targetScale.x
            obj.scale.y = targetScale.y
            obj.scale.z = targetScale.z
        } else {
            position.add(getStep(position, intermediatePosition, timeDelta, timeWindow * 1.5))
            intermediatePosition.add(getStep(intermediatePosition, targetPosition, timeDelta, timeWindow))

            orientation.slerp(intermediateOrientation, timeDelta / (2 * timeWindow))
            intermediateOrientation.slerp(targetOrientation, timeDelta / timeWindow)

            scale.add(getStep(scale, intermediateScale, timeDelta, timeWindow * 2.0))
            intermediateScale.add(getStep(intermediateScale, targetScale, timeDelta, timeWindow))

            obj.position.x = position.x
            obj.position.y = position.y
            obj.position.z = position.z
            obj.quaternion.x = orientation.x
            obj.quaternion.y = orientation.y
            obj.quaternion.z = orientation.z
            obj.quaternion.w = orientation.w
            obj.scale.x = scale.x
            obj.scale.y = scale.y
            obj.scale.z = scale.z
        }

        return !endOfInterpolation
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