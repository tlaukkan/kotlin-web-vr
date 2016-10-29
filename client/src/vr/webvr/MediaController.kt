package vr.webvr

import lib.threejs.*

class MediaController {

    val models: MutableMap<String, Object3D> = mutableMapOf()
    val textures: MutableMap<String, Texture> = mutableMapOf()

    val objLoader = OBJLoader()
    val textureLoader = TextureLoader()

    fun loadModel(path: String, onLoad: (path: String, model:Object3D) -> Unit) {
        this.objLoader.load(path, { obj ->
            this.models[path] = obj
            onLoad(path, obj)
        })
    }

    fun loadTexture(path: String, onLoad: (path: String, texture:Texture) -> Unit) {
        return this.textureLoader.load(path, { texture ->
            this.textures[path] = texture
            onLoad(path, texture)
        })
    }
}