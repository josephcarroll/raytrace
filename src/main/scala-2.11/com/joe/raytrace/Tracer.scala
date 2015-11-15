package com.joe.raytrace

import com.joe.raytrace.Geometry.Sphere

object Tracer {

  private def distanceTo(to: Vector)(from: Intersection): Double = {
    (to - from.point).length
  }

  def traceRay(scene: Scene)(ray: Ray): Vector = {
    val intersections = scene.spheres.flatMap(_.intersects(ray))
    val intersectionByDistance = intersections.sortBy(distanceTo(ray.origin))

    intersectionByDistance.headOption match {
      case Some(intersection) =>
        val lightInput = scene.lights.map { light =>
          val normal = (intersection.point - intersection.obj.position).normalize
          val lightRay = (light.position - normal).normalize
          val intensity = Math.max(0, lightRay.dot(normal))
          light.colour * intensity
        }
        intersection.obj.colour * (lightInput.foldLeft(Vector(0.0, 0.0, 0.0))(_ + _) + scene.ambientLight).cap(1.0)
      case None =>
        scene.backgroundColour
    }
  }
  
  case class Vector(x: Double, y: Double, z: Double) {

    def +(that: Vector): Vector = Vector(this.x + that.x, this.y + that.y, this.z + that.z)
    def -(that: Vector): Vector = Vector(this.x - that.x, this.y - that.y, this.z - that.z)
    def *(that: Vector): Vector = Vector(this.x * that.x, this.y * that.y, this.z * that.z)
    def *(constant: Double): Vector = Vector(this.x * constant, this.y * constant, this.z * constant)
    def /(constant: Double): Vector = Vector(this.x / constant, this.y / constant, this.z / constant)

    def negate: Vector = Vector(-this.x, -this.y, -this.z)
    def lengthSquared: Double = (x * x) + (y * y) + (z * z)
    def length: Double = Math.sqrt(lengthSquared)

    def cap(t: Double): Vector = Vector(Math.min(this.x, t), Math.min(this.y, t), Math.min(this.z, t))
    def floor(t: Double): Vector = Vector(Math.max(this.x, t), Math.max(this.y, t), Math.max(this.z, t))
    def bound(min: Double, max: Double): Vector = floor(min).cap(max)

    def dot(that: Vector): Double = {
      val m = this * that
      m.x + m.y + m.z
    }

    def normalize: Vector = {
      val normal = 1 / length
      Vector(x * normal, y * normal, z * normal)
    }

  }

  case class Ray(origin: Vector, direction: Vector) {
    def pointAt(t: Double): Vector = origin + (direction * t)
  }

  case class Intersection(point: Vector, obj: Sphere)


}