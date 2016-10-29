package lib.threejs

@native("THREE.OBJLoader")
class OBJLoader {
  @native fun load(path : String, callback: (obj: Object3D) -> Unit): Unit = noImpl
}
