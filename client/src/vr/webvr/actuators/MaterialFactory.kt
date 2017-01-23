package vr.webvr.actuators

import lib.threejs.Material
import lib.threejs.MeshPhongMaterial
import lib.threejs.Texture

/**
 * Factory for getting shared material instances.
 */
class MaterialFactory {

    val materials: MutableMap<String, Material> = mutableMapOf()

    fun getMaterial(textureName: String, texture: Texture, opacity: Double): Material {
        val materialKey = textureName + ":" + opacity

        if (materials.containsKey(materialKey)) {
            return materials[materialKey]!!
        }

        val material = MeshPhongMaterial(object {})
        material.map = texture
        material.opacity = opacity
        if (opacity < 1.0) {
            material.transparent = true
        }

        materials.put(materialKey, material)

        return material
    }

}