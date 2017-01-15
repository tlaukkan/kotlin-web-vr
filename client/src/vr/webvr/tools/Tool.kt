package vr.webvr.tools

import lib.threejs.Geometry
import lib.threejs.Line
import lib.threejs.MeshBasicMaterial
import lib.threejs.Vector3
import vr.webvr.devices.InputButton
import vr.webvr.devices.InputDevice
import kotlin.browser.window

/**
 * Created by tlaukkan on 11/1/2016.
 */
abstract class Tool(var name: String, var inputDevice: InputDevice) {

    protected var gripped = false

    abstract fun active()

    abstract fun deactivate()

    abstract fun render()

    abstract fun onPressed(button: InputButton)

    abstract fun onReleased(button: InputButton)

    abstract fun onSqueezed(button: InputButton, value: Double)

    abstract fun onPadTouched(x: Double, y: Double)
    
}