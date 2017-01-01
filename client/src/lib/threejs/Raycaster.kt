package lib.threejs

@native("THREE.Raycaster")
class Raycaster {
  @native fun set(origin : Vector3, direction: Vector3): Unit = noImpl
  @native fun intersectObjects(objects : Array<Object3D>, recursive: Boolean): Array<dynamic> = noImpl
}
