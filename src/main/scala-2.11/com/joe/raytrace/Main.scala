package com.joe.raytrace

import java.io.{File, FileOutputStream}

import com.joe.raytrace.Tracer._

object Main extends App {

  val scene = SceneProtocol.load()

  val camera = scene.camera

  val invWidth = 1.0 / camera.width
  val invHeight = 1.0 / camera.height
  val angle = Math.tan(Math.PI * camera.origin.z * camera.fieldOfView / 180.0)

  val image = for (x <- 0 until camera.width; y <- 0 until camera.height) yield {
    val xx = (2 * ((x + camera.origin.z) * invWidth) - 1) * angle * camera.aspectRatio
    val yy = (1 - 2 * ((y + camera.origin.z) * invHeight)) * angle
    val rayDirection = Vector(xx, yy, camera.direction.z).normalize
    traceRay(Ray(camera.origin, rayDirection))
  }

  val pixels = Array.fill(camera.pixelWidth * camera.pixelHeight)(Vector(0.0, 0.0, 0.0))

  for (x <- 0 until camera.width; y <- 0 until camera.height) {
    val realIndex = y + (camera.width * x)
    val index = (y / camera.antialiasing) + (camera.pixelWidth * (x / camera.antialiasing))
    pixels(index) += image(realIndex)
  }

  val finalPixels = pixels.map(_ / camera.samplesPerPixel)

  val bytes = finalPixels.flatMap { colour =>
    val r = (255 * colour.x).toByte
    val g = (255 * colour.y).toByte
    val b = (255 * colour.z).toByte
    Seq(r, g, b)
  }
  val f = new File("/Users/Joe/Desktop/result.ppm")
  if(!f.exists()) f.createNewFile()
  val fos = new FileOutputStream(f)
  fos.write(s"P6\n${camera.pixelWidth} ${camera.pixelHeight}\n255\n".getBytes)
  fos.write(bytes.toArray)
  fos.close()

  // Tracing logic!

  def distanceTo(to: Vector)(from: Intersection): Double = {
    (to - from.point).length
  }

  def traceRay(ray: Ray): Vector = {
    val intersections = scene.spheres.flatMap(_.intersects(ray))
    val intersectionByDistance = intersections.sortBy(distanceTo(ray.origin))

    intersectionByDistance.headOption match {
      case Some(intersection) =>
        val light = scene.lights.head
        val normal = (intersection.point - intersection.obj.position).normalize
        val lightRay = (light.position - normal).normalize
        val colour = Math.max(0, lightRay.dot(normal))
        intersection.obj.colour * light.colour * colour
      case None =>
        scene.backgroundColour
    }
  }

}
