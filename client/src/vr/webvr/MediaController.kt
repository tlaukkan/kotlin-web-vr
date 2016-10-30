package vr.webvr

import lib.threejs.*

class MediaController {

    val models: MutableMap<String, Object3D> = mutableMapOf()
    val textures: MutableMap<String, Texture> = mutableMapOf()

    val objLoader = OBJLoader()
    val colladaLoader = ColladaLoader()
    val textureLoader = TextureLoader()

    init {
        val dynamicColladaLoader: dynamic = colladaLoader
        dynamicColladaLoader.options.convertUpAxis = true
    }

    fun loadModel(path: String, onLoad: (path: String, model:Object3D) -> Unit) {
        if (path.endsWith(".obj")) {
            this.objLoader.load(path, { obj ->
                this.models[path] = obj
                onLoad(path, obj)
            })
        } else if (path.endsWith(".dae")) {
            this.colladaLoader.load(path, { collada ->
                this.models[path] = collada.scene
                onLoad(path, collada.scene)
            })
        }
    }

    fun loadTexture(path: String, onLoad: (path: String, texture:Texture) -> Unit) {
        return this.textureLoader.load(path, { texture ->
            this.textures[path] = texture
            onLoad(path, texture)
        })
    }
}