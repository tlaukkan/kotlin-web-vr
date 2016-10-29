package lib.threejs

@native("THREE.Mesh")
class Mesh(
  geometry: Geometry = noImpl,
  material: Material = noImpl
) : Object3D() {
}

@native("THREE.PointCloud")
class PointCloud(geometry: Geometry, material: Material) : Object3D() {

}

@native("THREE.Points")
class Points(
  geometry: Geometry = noImpl,
  material: Material = noImpl
) : Object3D() {
}
