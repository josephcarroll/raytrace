package com.joe.raytrace

import java.io.{File, FileOutputStream}

import com.joe.raytrace.Tracer._

object RayTracer extends App {

  val red = Sphere(Vector(2.0, 2.0, -15.0), 1.0, Vector(1.0, 0.0, 0.0))
  val white = Sphere(Vector(0.0, 0.0, -20.0), 1.0, Vector(1.0, 1.0, 1.0))
  val yellow = Sphere(Vector(-2.0, -2.0, -25.0), 1.0, Vector(1.0, 1.0, 0.0))
  val objects = Seq(white, red, yellow)

  val light = Light(Vector(10.0, 10.0, 5.0), Vector(0.9, 1.0, 0.9))
  val lights = Seq(light)

  val camera = Camera(Ray(Vector(0.0, 0.0, 0.5), Vector(0.0, 0.0, -1.0)), 600, 600)

  val invWidth = 1.0 / camera.width
  val invHeight = 1.0 / camera.height
  val angle = Math.tan(Math.PI * camera.origin.z * camera.fieldOfView / 180.0)

  val image = for (x <- 0 until camera.width; y <- 0 until camera.height) yield {

    val xx = (2 * ((x + camera.origin.z) * invWidth) - 1) * angle * camera.aspectRatio
    val yy = (1 - 2 * ((y + camera.origin.z) * invHeight)) * angle
    val rayDirection = Vector(xx, yy, camera.direction.z).normalize
    traceRay(Ray(camera.origin, rayDirection))

  }

  val bytes = image.flatMap { colour =>
    val r = (255 * colour.x).toByte
    val g = (255 * colour.y).toByte
    val b = (255 * colour.z).toByte
    Seq(r, g, b)
  }
  val f = new File("/Users/Joe/Desktop/result.ppm")
  if(!f.exists()) f.createNewFile()
  val fos = new FileOutputStream(f)
  fos.write(s"P6\n${camera.width} ${camera.height}\n255\n".getBytes)
  fos.write(bytes.toArray)
  fos.close()

  // Tracing logic!

  def distanceTo(to: Vector)(from: Intersection): T = {
    (to - from.point).length
  }

  def traceRay(ray: Ray): Vector = {
    val intersections = objects.flatMap(_.intersects(ray))
    val intersectionByDistance = intersections.sortBy(distanceTo(ray.origin))

    intersectionByDistance.headOption match {
      case Some(intersection) =>
        val normal = (intersection.point - intersection.obj.position).normalize
        val lightRay = (light.location - normal).normalize
        val colour = Math.max(0, lightRay.dot(normal))
        intersection.obj.colour * light.colour * colour
      case None =>
        Vector(0.0, 0.0, 0.0)
    }
  }

}

object Tracer {

  type T = Double

  case class Vector(x: T, y: T, z: T) {

    def +(that: Vector): Vector = Vector(this.x + that.x, this.y + that.y, this.z + that.z)
    def -(that: Vector): Vector = Vector(this.x - that.x, this.y - that.y, this.z - that.z)
    def *(that: Vector): Vector = Vector(this.x * that.x, this.y * that.y, this.z * that.z)
    def *(constant: T): Vector = Vector(this.x * constant, this.y * constant, this.z * constant)

    def negate: Vector = Vector(-this.x, -this.y, -this.z)
    def lengthSquared: T = (x * x) + (y * y) + (z * z)
    def length: T = Math.sqrt(lengthSquared)

    def dot(that: Vector): T = {
      val m = this * that
      m.x + m.y + m.z
    }

    def normalize: Vector = {
      val normal = 1 / length
      Vector(x * normal, y * normal, z * normal)
    }

  }

  case class Ray(origin: Vector, direction: Vector) {
    def pointAt(t: T): Vector = origin + (direction * t)
  }

  case class Intersection(point: Vector, obj: Sphere)

  case class Light(location: Vector, colour: Vector)

  case class Sphere(position: Vector, radius: T, colour: Vector) {

    val radiusSquared = radius * radius

    def intersects(ray: Ray): Option[Intersection] = {
      val L = position - ray.origin
      val tca = L.dot(ray.direction)
      if (tca < 0) None else {
        val d2 = L.dot(L) - (tca * tca)
        if (d2 > radiusSquared) None else {
          val thc = Math.sqrt(radiusSquared - d2)
          var t0 = tca - thc
          var t1 = tca + thc

          if (t0 > t1) {
            val tmp = t0
            t0 = t1
            t1 = tmp
          }

          if (t0 < 0) {
            t0 = t1 // if t0 is negative, let's use t1 instead
            if (t0 < 0) return None // both t0 and t1 are negative
          }

          val t = t0
          Some(Intersection(ray.pointAt(t), this))
        }
      }
    }

  }

  case class Camera(position: Ray, width: Int, height: Int, fieldOfView: T = 30.0) {

    val aspectRatio: T = {
      val heightAsDecimal: T = height // So we change T to a float without changing code!
      width / heightAsDecimal
    }

    def origin = position.origin

    def direction = position.direction

  }

}