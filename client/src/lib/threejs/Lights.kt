package lib.threejs

@native("THREE.Light")
open class Light(@native var color: Int) : Object3D() {
  @native
  var onlyShadow: Boolean = noImpl
  @native
  var shadowCameraNear: Int = noImpl
  @native
  var shadowCameraFar: Int = noImpl
  @native
  var shadowCameraLeft: Int = noImpl
  @native
  var shadowCameraRight: Int = noImpl
  @native
  var shadowCameraTop: Int = noImpl
  @native
  var shadowCameraBottom: Int = noImpl
  @native
  var shadowBias: Double = noImpl
  @native
  var shadowMapWidth: Int = noImpl
  @native
  var shadowMapHeight: Int = noImpl

  @native var shadowMap: dynamic = noImpl

}

@native("THREE.DirectionalLight")
class DirectionalLight(color: Int, intensity: Double = 1.0) : Light(color) {
  //Properties
  @native
  var target: Object3D = noImpl
  @native
  var intensity: Double = 0.0

}


@native("THREE.AmbientLight")
class AmbientLight(color: Int, intensity: Double = 1.0) : Light(color) {

}
@native("THREE.HemisphereLight")
open class HemisphereLight(skyColorHex: Int, groundColorHex: Int, intensity: Double) : Light(skyColorHex) {

  var groundColor: Color = noImpl
  var intensity: Number = noImpl

  fun copy(source: HemisphereLight): HemisphereLight = noImpl
  override fun  clone(recursive: Boolean): HemisphereLight = noImpl
}

@native("THREE.PointLight")
open class PointLight(color: Int, intensity: Double = 1.0, distance: Double = 0.0, decay: Double = 1.0) : Light(color) {

  fun copy(source: HemisphereLight): HemisphereLight = noImpl
  override fun  clone(recursive: Boolean): HemisphereLight = noImpl
}
