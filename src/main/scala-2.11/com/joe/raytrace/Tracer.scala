package com.joe.raytrace

import java.io.{File, FileOutputStream}

import com.joe.raytrace.Tracer._

object RayTracer extends App {

  val closeRed = Sphere(Vector(0.0, 0.0, -20.0), 2.0, Vector(1.0, 0.0, 0.0))
  val farYellow = Sphere(Vector(2.0, 2.0, -40.0), 6.0, Vector(1.0, 1.0, 0.0))
  val objects = Seq(closeRed, farYellow)

  val camera = Camera(Ray(Vector(0.0, 0.0, 0.0), Vector(0.0, 0.0, -1.0)), 600, 600)

  val invWidth = 1.0 / camera.width
  val invHeight = 1.0 / camera.height
  val angle = Math.tan(Math.PI * 0.5 * camera.fieldOfView / 180.0)

  val image = for (x <- 0 until camera.width; y <- 0 until camera.height) yield {

    val xx = (2 * ((x + 0.5) * invWidth) - 1) * angle * camera.aspectRatio
    val yy = (1 - 2 * ((y + 0.5) * invHeight)) * angle
    val rayDirection = Vector(xx, yy, camera.direction.z).normalize
    traceRay(Ray(camera.origin, rayDirection))

  }

  val colours = image.map {
    case Some(intersection) => intersection.obj.colour
    case None               => Vector(0.0, 0.0, 0.0)
  }

  val bytes = colours.flatMap { colour =>
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

  def traceRay(ray: Ray): Option[Intersection] = {
    val intersections = objects.flatMap(_.intersects(ray))
    val intersectionByDistance = intersections.sortBy(distanceTo(ray.origin))
    intersectionByDistance.headOption
  }

}

object Tracer {

  type T = Double

  case class Vector(x: T, y: T, z: T) {

    def +(that: Vector): Vector = Vector(this.x + that.x, this.y + that.y, this.z + that.z)
    def -(that: Vector): Vector = Vector(this.x - that.x, this.y - that.y, this.z - that.z)
    def *(that: Vector): Vector = Vector(this.x * that.x, this.y * that.y, this.z * that.z)

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

  case class Ray(origin: Vector, direction: Vector)

  case class Intersection(point: Vector, obj: Sphere)

  case class Sphere(center: Vector, radius: T, colour: Vector) {
    val radiusSquared = radius * radius
    def intersects(ray: Ray): Option[Intersection] = {
      val l = center - ray.origin
      val tca = l.dot(ray.direction)
      if (tca < 0.0) {
        None
      } else {
        val d2 = l.dot(l) - tca * tca
        if (d2 > radiusSquared) {
          None
        } else {
          // val thc = Math.sqrt(radiusSquared - d2)
          // t0 = tca - thc
          // t1 = tca + thc
          Some(Intersection(Vector(0.0, 0.0, 0.0), this))
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