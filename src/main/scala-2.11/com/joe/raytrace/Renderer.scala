package com.joe.raytrace

import com.joe.raytrace.Tracer._


object Renderer {

  def render(pixelWidth: Int, pixelHeight: Int, antialiasing: Int)(scene: Scene): Array[Vector] = {
    val width           = pixelWidth * antialiasing
    val height          = pixelHeight * antialiasing
    val samplesPerPixel = Math.pow(antialiasing, 2)
    val camera          = scene.camera

    def trace = traceRay(scene) _
    val image = Array.fill(width * height)(Vector.Zero)

    val invWidth = 1 / width.toDouble
    val invHeight = 1 / height.toDouble
    val fov = 45
    val aspectratio = width / height.toDouble
    val angle = Math.tan(Math.PI * 0.5 * fov / 180.0)

    for (y <- (0 until height).par; x <- (0 until width).par) {
      val xx = (2 * ((x + 0.5) * invWidth) - 1) * angle * aspectratio
      val yy = (1 - 2 * ((y + 0.5) * invHeight)) * angle
      val direction =  Vector(camera.direction.x + yy, camera.direction.y + xx, -1).normalize
      image(x + (width * y)) = trace(Ray(camera.origin, direction))
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
