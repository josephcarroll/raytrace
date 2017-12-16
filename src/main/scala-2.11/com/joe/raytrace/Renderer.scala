package com.joe.raytrace

import com.joe.raytrace.Tracer._


object Renderer {

  def render(pixelWidth: Int, pixelHeight: Int, antialiasing: Int)(scene: Scene): Array[Vector] = Timer.time("rendering") {
    val renderWidth     = pixelWidth * antialiasing
    val renderHeight    = pixelHeight * antialiasing
    val samplesPerPixel = Math.pow(antialiasing, 2)
    val camera          = scene.camera

    def trace = traceRay(scene) _
    val image = Array.fill(renderWidth * renderHeight)(Vector.Zero)

    val invWidth = 1 / renderWidth.toDouble
    val invHeight = 1 / renderHeight.toDouble
    val aspectRatio = renderWidth / renderHeight.toDouble
    val angle = Math.tan(Math.PI * 0.5 * camera.fov / 180.0)

    for (y <- (0 until renderHeight).par; x <- (0 until renderWidth).par) {
      val xx = (2 * ((x + 0.5) * invWidth) - 1) * angle * aspectRatio
      val yy = (1 - 2 * ((y + 0.5) * invHeight)) * angle
      val direction = Vector(camera.direction.x + xx, camera.direction.y + yy, camera.direction.z).normalize
      image(x + (renderWidth * y)) = trace(Ray(camera.origin, direction))
    }

    val pixels = Array.fill(pixelWidth * pixelHeight)(Vector.Zero)
    for (y <- 0 until renderHeight; x <- 0 until renderWidth) {
      val realIndex = x + (renderWidth * y)
      val index = (x / antialiasing) + (pixelWidth * (y / antialiasing))
      pixels(index) += image(realIndex)
    }

    pixels.map(_ / samplesPerPixel)
  }

}
