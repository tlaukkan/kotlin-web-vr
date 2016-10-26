package webvrapi

import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.Navigator
import org.w3c.dom.Window
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventTarget

@native
open class VRDisplay : EventTarget {
    open var isConnected: Boolean = noImpl
    open var isPresenting: Boolean = noImpl
    open var capabilities: VRDisplayCapabilities = noImpl
    open var stageParameters: VRStageParameters = noImpl
    open fun getEyeParameters(whichEye: Any): VREyeParameters = noImpl
    open var displayId: Number = noImpl
    open var displayName: String = noImpl
    open fun getPose(): VRPose = noImpl
    open fun getImmediatePose(): VRPose = noImpl
    open fun resetPose(): Unit = noImpl
    open var depthNear: Number = noImpl
    open var depthFar: Number = noImpl
    open fun requestAnimationFrame(callback: (Number)->Unit): Number = noImpl
    open fun cancelAnimationFrame(handle: Number): Unit = noImpl
    open fun requestPresent(layers: Array<VRLayer>): Promise<Unit> = noImpl
    open fun exitPresent(): Promise<Unit> = noImpl
    open fun getLayers(): Array<VRLayer> = noImpl
    open fun submitFrame(pose: VRPose? = null): Unit = noImpl
}

@native
open class VRLayer {
    open var source: HTMLCanvasElement = noImpl
    open var leftBounds: Array<Number> = noImpl
    open var rightBounds: Array<Number> = noImpl
}
@native
interface VRDisplayCapabilities {
    var hasPosition: Boolean
    var hasOrientation: Boolean
    var hasExternalDisplay: Boolean
    var canPresent: Boolean
    var maxLayers: Number
}
@native
interface VRFieldOfView {
    var upDegrees: Number
    var rightDegrees: Number
    var downDegrees: Number
    var leftDegrees: Number
}
@native
interface VRPose {
    var timestamp: Number
    var position: Float32Array
    var linearVelocity: Float32Array
    var linearAcceleration: Float32Array
    var orientation: Float32Array
    var angularVelocity: Float32Array
    var angularAcceleration: Float32Array
}
@native
interface VREyeParameters {
    var offset: Float32Array
    var fieldOfView: VRFieldOfView
    var renderWidth: Number
    var renderHeight: Number
}
@native
interface VRStageParameters {
    var sittingToStandingTransform: Float32Array
    var sizeX: Number
    var sizeZ: Number
}
@native
fun Navigator.getVRDisplays(): Promise<Array<VRDisplay>> = noImpl
@native
var Navigator.activeVRDisplays: Array<VRDisplay> get() = noImpl
@native
var Window.onvrdisplayconnected: (ev: Event) -> Any get() = noImpl
@native
var Window.onvrdisplaydisconnected: (ev: Event) -> Any get() = noImpl
@native
var Window.onvrdisplaypresentchange: (ev: Event) -> Any get() = noImpl
@native
fun Window.addEventListener(type: Any /* "vrdisplayconnected"*/, listener: (ev: Event) -> Any, useCapture: Boolean? = null): Unit = noImpl
/*
@native
fun Window.addEventListener(type: Any /* "vrdisplaydisconnected"*/, listener: (ev: Event) -> Any, useCapture: Boolean? = null): Unit = noImpl
@native
fun Window.addEventListener(type: Any /* "vrdisplaypresentchange"*/, listener: (ev: Event) -> Any, useCapture: Boolean? = null): Unit = noImpl
*/
@native
interface Gamepad {
    var displayId: Number
}


@native
val navigator: Navigator = noImpl