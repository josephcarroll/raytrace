package com.joe.raytrace

import com.joe.raytrace.Scene.Light

object Tracer {

  private def distanceTo(to: Vector)(from: Intersection): Double = {
    (to - from.point).length
  }

  def traceRay(scene: Scene)(ray: Ray): Vector = {
    val intersections = scene.shapes.flatMap(_.intersects(ray))
    val intersectionByDistance = intersections.sortBy(distanceTo(ray.origin))

    intersectionByDistance.headOption match {
      case Some(intersection) =>
        val lightInput = scene.lights.map(colourFromLight(intersection, scene))
        val lightSum = lightInput.foldLeft(Vector.Zero)(_ + _)
        (scene.ambientLight + lightSum).cap(1.0)
      case None =>
        scene.backgroundColour
    }
  }

  private def colourFromLight(intersection: Intersection, scene: Scene)(light: Light): Vector = {
    val normal = intersection.obj.normal(intersection.point)
    val lightRay = (light.position - intersection.point).normalize
    val intensity = Math.max(0, lightRay.dot(normal))

    val inShade = if (blocked(scene, intersection.obj, Ray(intersection.point, lightRay))) 0.0 else 1.0
    intersection.obj.colour * light.colour * inShade * intensity
  }

  private def blocked(scene: Scene, self: Shape, ray: Ray): Boolean = {
    val possibleBlockers = scene.shapes.filterNot(_ == self)
    if (possibleBlockers.isEmpty) false else possibleBlockers.exists(_.intersects(ray).isDefined)
  }

  case class Ray(origin: Vector, direction: Vector) {
    def pointAt(t: Double): Vector = origin + (direction * t)
  }

  case class Intersection(point: Vector, obj: Shape)


}