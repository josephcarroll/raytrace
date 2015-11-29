package com.joe.raytrace

import com.joe.raytrace.Tracer._

object Main extends App {

  val (Array(widthInput, heightInput, antialiasingInput), inputs) = args.splitAt(3)

  val antialiasing    = antialiasingInput.toInt
  val pixelWidth      = widthInput.toInt
  val pixelHeight     = heightInput.toInt
  val width           = pixelWidth * antialiasing
  val height          = pixelHeight * antialiasing
  val samplesPerPixel = Math.pow(antialiasing, 2)

  println(s"Sample resolution: ($width, $height). Effective Resolution: ($pixelWidth, $pixelHeight)")

  time("All processing") {
    inputs.par.foreach(render)
  }

  def render(name: String): Unit = {
    val scene = SceneProtocol.load(name)
    val camera = scene.camera

    def trace = traceRay(scene) _

    val image = Array.fill(width * height)(Vector.Zero)
    time(name + " rendering") {
      for (y <- 0 until height; x <- 0 until width) {
        val xOffset = 1.0 - (2.0 * (x / width.toDouble))
        val yOffset = 1.0 - (2.0 * (y / height.toDouble))
        val direction = camera.direction + Vector(yOffset, xOffset, 0.0)
        image(x + (width * y)) = trace(Ray(camera.origin, direction.normalize))
      }
    }

  val pixels = Array.fill(pixelWidth * pixelHeight)(Vector.Zero)
    for (y <- 0 until height; x <- 0 until width) {
      val realIndex = x + (width * y)
      val index = (x / antialiasing) + (pixelWidth * (y / antialiasing))
      pixels(index) += image(realIndex)
    }
    val finalPixels = pixels.map(_ / samplesPerPixel)

    FileRenderer.renderToFile(pixelWidth, pixelHeight, finalPixels, name)
  }

  private def time[T](name: String)(func: => T): T= {
    val before = System.currentTimeMillis()
    val result = func
    val total = System.currentTimeMillis() - before
    println(s"$name completed in ${total}ms")
    result
  }

}
