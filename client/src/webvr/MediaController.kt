package webvr

import threejs.OBJLoader
import threejs.Object3D
import threejs.Texture
import threejs.TextureLoader

class MediaController {

    val models: MutableMap<String, Object3D> = mutableMapOf()
    val textures: MutableMap<String, Texture> = mutableMapOf()

    val objLoader = OBJLoader()
    val textureLoader = TextureLoader()

    fun loadModel(name: String, path: String, onLoad: (name: String, model:Object3D) -> Unit) {
        this.objLoader.load(path, { obj ->
            this.models[name] = obj
            onLoad(name, obj)
        })
    }

    fun loadTexture(path: String, onLoad: (name: String, texture:Texture) -> Unit) {
        return this.textureLoader.load(path, { texture ->
            this.textures[path] = texture
            onLoad(path, texture)
        })
    }
}