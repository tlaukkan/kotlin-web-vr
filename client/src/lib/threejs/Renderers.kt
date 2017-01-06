package lib.threejs

import org.w3c.dom.Element

@native("THREE.WebGLRenderer")
open class WebGLRenderer(parameters: Any) {
  @native var domElement: Element = noImpl
  @native var sortObjects: Boolean = noImpl
  @native var physicallyCorrectLights: Boolean = noImpl
  @native var gammaInput: Boolean = noImpl
  @native var gammaOutput: Boolean = noImpl
  @native var shadowMapEnabled: Boolean = noImpl
  @native var shadowMapWidth: Int = noImpl
  @native var shadowMapHeight: Int = noImpl
  @native var shadowMap: dynamic = noImpl

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

@native("THREE.BasicShadowMap") var BasicShadowMap: dynamic = noImpl