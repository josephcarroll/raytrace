package com.joe.raytrace

import com.joe.raytrace.Tracer._

trait Shape {
  def intersects(ray: Ray): Option[Intersection]
  def colour: Vector
  def normal(point: Vector): Vector
}

case class Plane(position: Vector, normal: Vector, colour: Vector) extends Shape {

  override def normal(point: Vector): Vector = normal

  override def intersects(ray: Ray): Option[Intersection] = {
    val denominator = normal.dot(ray.direction)

    if (denominator >= 1e-6) {
      val t = (position - ray.origin).dot(normal) / denominator
      if (t >= 0) Some(Intersection(ray.pointAt(t), this)) else None
    } else {
      None
    }
  }

}

case class Sphere(position: Vector, radius: Double, colour: Vector) extends Shape {

  override def normal(point: Vector): Vector = (point - position).normalize

  override def intersects(ray: Ray): Option[Intersection] = {
    val radiusSquared = radius * radius

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