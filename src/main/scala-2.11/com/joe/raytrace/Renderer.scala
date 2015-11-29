package com.joe.raytrace

import com.joe.raytrace.Tracer._


object Renderer {

  def render(pixelWidth: Int, pixelHeight: Int, antialiasing: Int)(scene: Scene): Array[Vector] = {
    val width           = pixelWidth * antialiasing
    val height          = pixelHeight * antialiasing
    val samplesPerPixel = Math.pow(antialiasing, 2)
    val aspectRatio     = width / height
    val camera          = scene.camera

    val imageWidth      = 1.0
    val imageWidthX2    = imageWidth * 2.0
    val imageHeight     = 1.0 * aspectRatio
    val imageHeightX2   = imageWidth * 2.0

    def trace = traceRay(scene) _

    val image = Array.fill(width * height)(Vector.Zero)
    for (y <- (0 until height).par; x <- (0 until width).par) {
      val xOffset = imageWidth - (imageWidthX2 * (x / width.toDouble))
      val yOffset = imageHeight - (imageHeightX2 * (y / height.toDouble))
      val direction = camera.direction + Vector(yOffset, xOffset, 0.0)
      image(x + (width * y)) = trace(Ray(camera.origin, direction.normalize))
    }

    val pixels = Array.fill(pixelWidth * pixelHeight)(Vector.Zero)
    for (y <- (0 until height).par; x <- (0 until width).par) {
      val realIndex = x + (width * y)
      val index = (x / antialiasing) + (pixelWidth * (y / antialiasing))
      pixels(index) += image(realIndex)
    }

    pixels.map(_ / samplesPerPixel)
  }

}
