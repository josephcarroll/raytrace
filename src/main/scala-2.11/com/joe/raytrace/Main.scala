package com.joe.raytrace

import com.joe.raytrace.Tracer._

object Main extends App {

  time("All processing") {
    args.par.foreach(render)
  }

  def render(name: String): Unit = {
    val scene = SceneProtocol.load(name)
    val camera = scene.camera

    def trace = traceRay(scene) _

    val image = Array.fill(camera.width * camera.height)(Vector.Zero)
    time(name + " rendering") {
      for (y <- 0 until camera.height; x <- 0 until camera.width) {
        val xOffset = 1.0 - (2.0 * (x / camera.width.toDouble))
        val yOffset = 1.0 - (2.0 * (y / camera.height.toDouble))
        val direction = camera.direction + Vector(yOffset, xOffset, 0.0)
        image(x + (camera.width * y)) = trace(Ray(camera.origin, direction.normalize))
      }
    }

  val pixels = Array.fill(camera.pixelWidth * camera.pixelHeight)(Vector.Zero)
    for (y <- 0 until camera.height; x <- 0 until camera.width) {
      val realIndex = x + (camera.width * y)
      val index = (x / camera.antialiasing) + (camera.pixelWidth * (y / camera.antialiasing))
      pixels(index) += image(realIndex)
    }
    val finalPixels = pixels.map(_ / camera.samplesPerPixel)

    FileRenderer.renderToFile(camera.pixelWidth, camera.pixelHeight, finalPixels, name)
  }

  private def time[T](name: String)(func: => T): T= {
    val before = System.currentTimeMillis()
    val result = func
    val total = System.currentTimeMillis() - before
    println(s"$name completed in ${total}ms")
    result
  }

}
