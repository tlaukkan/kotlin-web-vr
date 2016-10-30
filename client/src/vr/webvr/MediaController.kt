package vr.webvr

import lib.threejs.*

class MediaController {

    val models: MutableMap<String, Object3D> = mutableMapOf()

    val textures: MutableMap<String, Texture> = mutableMapOf()
    val texturesLoading: MutableMap<String, Boolean> = mutableMapOf()
    val textureWaiters: MutableMap<String, MutableList<(path: String, texture:Texture) -> Unit>> = mutableMapOf()

    val objLoader = OBJLoader()
    val colladaLoader = ColladaLoader()
    val textureLoader = TextureLoader()
    val jsonLoader = JSONLoader()

    init {
        val dynamicColladaLoader: dynamic = colladaLoader
        dynamicColladaLoader.options.convertUpAxis = true
    }

    fun loadModel(path: String, onLoad: (path: String, model:Object3D) -> Unit) {
        if (path.endsWith(".obj")) {
            this.objLoader.load(path, { obj ->
                this.models[path] = obj
                println("Loaded OBJ model: " + path)
                onLoad(path, obj)
            })
        } else if (path.endsWith(".dae")) {
            this.colladaLoader.load(path, { collada ->
                this.models[path] = collada.scene
                println("Loaded Collada model: " + path)
                onLoad(path, collada.scene)
            })
        } else if (path.endsWith(".js")) {
            this.jsonLoader.load(path, { geometry, materials ->
                val faceMaterial = MultiMaterial(materials)
                var mesh = Mesh(geometry, faceMaterial)
                this.models[path] = mesh
                println("Loaded JSON model: " + path)
                onLoad(path, mesh)
            })
        }
    }

    fun loadTexture(path: String, onLoad: (path: String, texture:Texture) -> Unit) {
        if (texturesLoading[path] != null && texturesLoading[path]!!) {
            if (!textureWaiters.containsKey(path)) {
                textureWaiters[path] = mutableListOf()
            }
            textureWaiters[path]!!.add(onLoad)
            return
        }
        if (texturesLoading[path] != null && !texturesLoading[path]!!) {
            onLoad(path, textures[path]!!)
            return
        }
        texturesLoading[path] = true
        return this.textureLoader.load(path, { texture ->
            this.textures[path] = texture
            texturesLoading[path] = false
            println("Loaded texture: " + path)
            onLoad(path, texture)
            if (textureWaiters.containsKey(path)) {
                for (waiterOnLoad in textureWaiters[path]) {
                    waiterOnLoad(path, texture)
                    println("Waiter onload for texture: " + path)
                }
                textureWaiters.remove(path)
            }
        })
    }
}