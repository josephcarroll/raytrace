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

          val inShade = if (blocked(scene, Ray(intersection.point + normal, lightRay))) 0.0 else 1.0
          light.colour * inShade * intensity
        }
        intersection.obj.colour * (lightInput.foldLeft(Vector(0.0, 0.0, 0.0))(_ + _) + scene.ambientLight).cap(1.0)
      case None =>
        scene.backgroundColour
    }
  }

  private def blocked(scene: Scene, ray: Ray): Boolean = {
    scene.spheres.exists(_.intersects(ray).isDefined)
  }

  case class Ray(origin: Vector, direction: Vector) {
    def pointAt(t: Double): Vector = origin + (direction * t)
  }

  case class Intersection(point: Vector, obj: Sphere)


}