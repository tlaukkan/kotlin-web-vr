package threejs

@native("THREE.Light")
open class Light(@native var color: Int) : Object3D()

@native("THREE.DirectionalLight")
class DirectionalLight(color: Int, intensity: Double = 1.0) : Light(color) {
  //Properties
  @native
  var target: Object3D = noImpl
  @native
  var intensity: Double = 0.0
  @native
  var onlyShadow: Boolean = false
  @native
  var shadowCameraNear: Int = 50
  @native
  var shadowCameraFar: Int = 5000
  @native
  var shadowCameraLeft: Int = -500
  @native
  var shadowCameraRight: Int = 500
  @native
  var shadowCameraTop: Int = 500
  @native
  var shadowCameraBottom: Int = -500
  @native
  var shadowBias: Double = 0.0
  @native
  var shadowMapWidth: Int = 512
  @native
  var shadowMapHeight: Int = 512
}

@native("THREE.HemisphereLight")
open class HemisphereLight(skyColorHex: Int, groundColorHex: Int, intensity: Double) : Light(skyColorHex) {

  var groundColor: Color
  var intensity: Number

  fun copy(source: HemisphereLight): HemisphereLight
  override fun  clone(recursive: Boolean): HemisphereLight
}
