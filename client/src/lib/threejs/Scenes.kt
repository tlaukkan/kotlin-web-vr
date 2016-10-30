package lib.threejs

@native("THREE.Scene")
open class Scene : Object3D() {
  //Properties
  @native var fog: FogExp2 = noImpl
  @native var autoUpdate: Boolean = noImpl

  //Functions
  fun add(a: Any): Unit = noImpl
  fun remove(a: Any): Unit = noImpl
  fun getObjectByName(name: String): Object3D? = noImpl

}

@native("THREE.FogExp2")
class FogExp2(color: Color, density: Double) {
  //Properties
  @native
  var name: String = noImpl
  @native
  var color: Color = noImpl
  @native
  var density: Double = noImpl
  //Functions
  fun clone(): FogExp2 = noImpl
}
