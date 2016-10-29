package lib.threejs

@native("THREE.Texture")
open class Texture {

}

@native("THREE.TextureLoader")
open class TextureLoader {
  @native fun load(
    url: String,
    callback: (Texture) -> Unit
  ) :Unit = noImpl
}
