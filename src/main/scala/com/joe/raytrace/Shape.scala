package com.joe.raytrace

import com.joe.raytrace.Tracer._

trait Shape {
  def intersects(ray: Ray): Double
  def material: Material
  def normal(point: Vector): Vector
  def castsShadow: Boolean = true
}

object Shape {
  val MaxDistance = 10000.0
  val MinDistance = 1e-06
}

case class Plane(position: Vector, normal: Vector, material: Material) extends Shape {

  override def normal(point: Vector): Vector = normal

  override def intersects(ray: Ray): Double = {
    val denominator = normal.dot(ray.direction)

    if (denominator < Shape.MinDistance) {
      val t = -ray.origin.dot(normal) / denominator
      if (t < Shape.MaxDistance) t else Double.MaxValue
    } else {
      Double.MaxValue
    }
  }

}

case class Sphere(position: Vector, radius: Double, material: Material) extends Shape {

  override def normal(point: Vector): Vector = (point - position).normalize

  override def intersects(ray: Ray): Double = {
    val radiusSquared = radius * radius
    val e = position - ray.origin
    val a = ray.direction.dot(e)
    val f = radiusSquared - e.dot(e) + (a * a)

    if (f < Shape.MinDistance) {
      Double.MaxValue
    } else {
      val t = a - Math.sqrt(f)
      if (t > Shape.MinDistance && t < Shape.MaxDistance) {
        t
      } else {
        Double.MaxValue
      }
    }
  }

}