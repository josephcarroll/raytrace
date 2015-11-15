package com.joe.raytrace

import com.joe.raytrace.Tracer._

object Main extends App {

  val scene = SceneProtocol.load(args(0))
  def trace = traceRay(scene) _

  val camera = scene.camera

  val invWidth = 1.0 / camera.width
  val invHeight = 1.0 / camera.height
  val angle = Math.tan(Math.PI * camera.origin.z * camera.fieldOfView / 180.0)

  val image = for (x <- 0 until camera.width; y <- 0 until camera.height) yield {
    val xx = (2 * ((x + camera.origin.z) * invWidth) - 1) * angle * camera.aspectRatio
    val yy = (1 - 2 * ((y + camera.origin.z) * invHeight)) * angle
    val rayDirection = Vector(yy, xx, camera.direction.z).normalize
    trace(Ray(camera.origin, rayDirection))
  }

  val pixels = Array.fill(camera.pixelWidth * camera.pixelHeight)(Vector(0.0, 0.0, 0.0))

  for (x <- 0 until camera.width; y <- 0 until camera.height) {
    val realIndex = y + (camera.width * x)
    val index = (y / camera.antialiasing) + (camera.pixelWidth * (x / camera.antialiasing))
    pixels(index) += image(realIndex)
  }

  val finalPixels = pixels.map(_ / camera.samplesPerPixel)
  FileRenderer.renderToFile(camera.pixelWidth, camera.pixelHeight, finalPixels)


}
