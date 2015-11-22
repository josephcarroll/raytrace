package com.joe.raytrace

import com.joe.raytrace.Tracer._

object Main extends App {

  args.par.foreach(render)

  def render(name: String): Unit = {
    val before = System.currentTimeMillis()

    val scene = SceneProtocol.load(name)
    val camera = scene.camera

    def trace = traceRay(scene) _

    val image = for (y <- 0 until camera.height; x <- 0 until camera.width) yield {
      val xOffset = 1.0 - (2.0 * (x / camera.width.toDouble))
      val yOffset = 1.0 - (2.0 * (y / camera.height.toDouble))
      val direction = camera.direction + Vector(yOffset, xOffset, 0.0)
      trace(Ray(camera.origin, direction.normalize))
    }

    val pixels = Array.fill(camera.pixelWidth * camera.pixelHeight)(Vector.Zero)
    for (y <- 0 until camera.height; x <- 0 until camera.width) {
      val realIndex = x + (camera.width * y)
      val index = (x / camera.antialiasing) + (camera.pixelWidth * (y / camera.antialiasing))
      pixels(index) += image(realIndex)
    }
    val finalPixels = pixels.map(_ / camera.samplesPerPixel)

    val duration = System.currentTimeMillis() - before
    println(s"$name rendered in ${duration}ms")
    FileRenderer.renderToFile(camera.pixelWidth, camera.pixelHeight, finalPixels, name)
  }

}
