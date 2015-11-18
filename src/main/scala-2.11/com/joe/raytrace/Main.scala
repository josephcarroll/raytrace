package com.joe.raytrace

import com.joe.raytrace.Tracer._

object Main extends App {

  args.par.foreach(render)

  def render(name: String): Unit = {
    val before = System.currentTimeMillis()

    val scene = SceneProtocol.load(name)
    def trace = traceRay(scene) _

    val camera = scene.camera
    val invWidth = 1.0 / camera.width
    val invHeight = 1.0 / camera.height
    val angle = Math.tan(Math.PI * 0.5 * camera.fieldOfView / 180.0)

    val image = for (y <- 0 until camera.height; x <- 0 until camera.width) yield {
      val xx = (2 * ((x + camera.origin.z) * invWidth) - 1) * angle * camera.aspectRatio
      val yy = (1 - 2 * ((y + camera.origin.z) * invHeight)) * angle
      val rayDirection = Vector(yy, xx, camera.direction.z).normalize
      trace(Ray(camera.origin, rayDirection))
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
