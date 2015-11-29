package com.joe.raytrace.scenes

import com.joe.raytrace.Tracer._
import com.joe.raytrace.{Scene, Vector}


object Renderer {

  def render(pixelWidth: Int, pixelHeight: Int, antialiasing: Int)(scene: Scene): Array[Vector] = {
    val width           = pixelWidth * antialiasing
    val height          = pixelHeight * antialiasing
    val samplesPerPixel = Math.pow(antialiasing, 2)
    val camera          = scene.camera

    def trace = traceRay(scene) _

    val image = Array.fill(width * height)(Vector.Zero)
    for (y <- (0 until height).par; x <- (0 until width).par) {
      val xOffset = 1.0 - (2.0 * (x / width.toDouble))
      val yOffset = 1.0 - (2.0 * (y / height.toDouble))
      val direction = camera.direction + Vector(yOffset, xOffset, 0.0)
      image(x + (width * y)) = trace(Ray(camera.origin, direction.normalize))
    }

    val pixels = Array.fill(pixelWidth * pixelHeight)(Vector.Zero)
    for (y <- 0 until height; x <- 0 until width) {
      val realIndex = x + (width * y)
      val index = (x / antialiasing) + (pixelWidth * (y / antialiasing))
      pixels(index) += image(realIndex)
    }

    pixels.map(_ / samplesPerPixel)
  }

}
