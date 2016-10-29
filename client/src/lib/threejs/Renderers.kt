package lib.threejs

import org.w3c.dom.Element

@native("THREE.WebGLRenderer")
open class WebGLRenderer {
  @native var domElement: Element = noImpl
  @native var autoClear: Boolean = noImpl
  @native var sortObjects: Boolean = noImpl

  //Functions
  fun setSize(innerWidth: Double, innerHeight: Double): Unit = noImpl

  fun setClearColor(color: Int): Unit = noImpl
  @native fun render(scene: Scene, camera: PerspectiveCamera): Unit = noImpl
  @native fun clear(): Unit = noImpl
  @native fun clearDepth(): Unit = noImpl

  @native fun setViewport(x: Number, y: Number, width: Number, height: Number): Unit = noImpl
  @native fun setScissor(x: Number, y: Number, width: Number, height: Number): Unit = noImpl
  @native fun setScissorTest(enable: Boolean): Unit = noImpl

  @native fun getPixelRatio(): Number = noImpl
  @native fun setPixelRatio(value: Number): Unit = noImpl

}
