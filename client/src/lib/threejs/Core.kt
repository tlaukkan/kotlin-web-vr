package lib.threejs

@native("THREE.Geometry")
open class Geometry {
  //Properties
  @native
  var id: Int = noImpl
  @native
  var name: String = noImpl
  @native
  var vertices: Array<Vector3> = noImpl
  @native
  var colors: Array<Color> = noImpl
  //@native var faces: Array<Triangle> = noImpl
  //@native var faceVertexUvs: Array<UV> = noImpl
  //@native var morphTargets: Vector3 = noImpl
  //@native var morphColors: Vector3 = noImpl
  //@native var morphNormals: Vector3 = noImpl
  @native
  var skinWeights: Vector3 = noImpl
  @native
  var skinIndices: Vector3 = noImpl
  //@native var boundingBox: Vector3 = noImpl
  @native
  var boundingSphere: Double = noImpl
  @native
  var hasTangents: Boolean = noImpl
  @native
  var dynamic: Boolean = noImpl
  @native
  var verticesNeedUpdate: Boolean = noImpl
  @native
  var elementsNeedUpdate: Boolean = noImpl
  @native
  var uvsNeedUpdate: Boolean = noImpl
  @native
  var normalsNeedUpdate: Boolean = noImpl
  @native
  var tangentsNeedUpdate: Boolean = noImpl
  @native
  var colorsNeedUpdate: Boolean = noImpl
  @native
  var lineDistancesNeedUpdate: Boolean = noImpl
  @native
  var buffersNeedUpdate: Boolean = noImpl
  @native
  var lineDistances: Array<Double> = noImpl

  //Functions
  fun applyMatrix(m: Matrix4): Unit = noImpl

  fun computeFaceNormals(): Unit = noImpl
  fun computeVertexNormals(): Unit = noImpl
  fun computeMorphNormals(): Unit = noImpl
  fun computeTangents(): Unit = noImpl
  fun computeBoundingBox(): Unit = noImpl
  fun computeBoundingSphere(): Unit = noImpl
  fun merge(geometry: Geometry, m: Matrix4, materialIndexOffset: Int): Unit = noImpl
  fun mergeVertices(): Unit = noImpl
  fun makeGroups(m: Matrix4): Unit = noImpl
  fun clone(m: Matrix4): Unit = noImpl
  fun dispose(m: Matrix4): Unit = noImpl
  fun computeLineDistances(m: Matrix4): Unit = noImpl
}

@native("THREE.PlaneGeometry")
open class PlaneGeometry(width: Double, height: Double) : Geometry() {

}

@native("THREE.Object3D")
open class Object3D(
  @native var parent: Object3D = noImpl,
  @native var children: Array<Object3D> = noImpl,
  @native var position: Vector3 = noImpl,
  @native var rotation: Euler = noImpl,
  @native var scale: Vector3 = noImpl
) {
  @native var castShadow: Boolean = noImpl
  @native var receiveShadow: Boolean = noImpl
  @native var shadow: dynamic = noImpl
  @native var int: Int = noImpl
  @native var uuid: String = noImpl
  @native var name: String = noImpl
  @native var matrix: Matrix4 = noImpl
  @native var matrixWorld: Matrix4 = noImpl
  @native var quaternion: Quaternion = noImpl
  @native var matrixAutoUpdate: Boolean = noImpl
  @native var matrixWorldNeedsUpdate: Boolean = noImpl
  @native var material: Material = noImpl

  @native var visible: Boolean
  //Functions
  fun add(obj: Object3D): Unit = noImpl
  fun remove(obj: Object3D): Unit = noImpl

  @native fun rotateX(radians: Double): Unit = noImpl
  @native fun rotateY(radians: Double): Unit = noImpl
  @native fun rotateZ(radians: Double): Unit = noImpl

  @native fun updateMatrix(): Unit = noImpl
  @native fun updateMatrixWorld(): Unit = noImpl

  @native fun translateOnAxis(axis: Vector3, distance: Number): Object3D = noImpl

  @native fun applyMatrix(matrix: Matrix4): Unit = noImpl

  @native fun getWorldPosition(optionalTarget: Vector3): Vector3 = noImpl

  @native fun getWorldQuaternion(optionalTarget: Quaternion): Quaternion = noImpl

  @native fun getWorldRotation(optionalTarget: Quaternion): Quaternion = noImpl

  @native open fun clone(recursive: Boolean): Object3D = noImpl
}

@native("THREE.Group")
open class Group() : Object3D() {

}