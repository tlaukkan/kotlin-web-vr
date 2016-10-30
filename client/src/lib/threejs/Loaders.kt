package lib.threejs

@native("THREE.OBJLoader")
class OBJLoader {
  @native fun load(path : String, callback: (obj: Object3D) -> Unit): Unit = noImpl
}

@native("THREE.JSONLoader")
class JSONLoader {
  @native fun load(path : String, callback: (geometry: Geometry, materials: Array<Material>) -> Unit): Unit = noImpl
}


@native
interface Collada {

  var scene: Object3D
//  morphs: morphs,
//  skins: skins,
//  animations: animData,
//  kinematics: kinematics,
//  dae: {
//    images: images,
//    materials: materials,
//    cameras: cameras,
//    lights: lights,
//    effects: effects,
//    geometries: geometries,
//    controllers: controllers,
//    animations: animations,
//    visualScenes: visualScenes,
//    visualScene: visualScene,
//    scene: visualScene,
//    kinematicsModels: kinematicsModels,
//    kinematicsModel: kinematicsModel
//  }

}

@native("THREE.ColladaLoader")
class ColladaLoader {
  @native fun load(path : String, callback: (collada: Collada) -> Unit): Unit = noImpl
}
