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
        val lightInput = scene.lights.map(colourFromLight(intersection, scene, ray))
        val lightSum = lightInput.foldLeft(Vector.Zero)(_ + _)
        (scene.ambientLight + lightSum).cap(1.0)
      case None =>
        scene.backgroundColour
    }
  }

  private def colourFromLight(intersection: Intersection, scene: Scene, viewerRay: Ray)(light: Light): Vector = {
    val normal = intersection.obj.normal(intersection.point)
    val lightRay = (light.position - intersection.point).normalize

    val diffuseIntensity = Math.max(0.0, lightRay.dot(normal))

    val reflectedRay = lightRay - (normal * 2.0 * normal.dot(lightRay)) // l - 2(n.l)n
    val specularIntensity = Math.pow(Math.max(0.0, viewerRay.direction.dot(reflectedRay)), 32.0)

    val inShade = blocked(scene, intersection.obj, Ray(intersection.point, lightRay))
    if(inShade) Vector.Zero
    else (intersection.obj.colour * light.colour * diffuseIntensity) + (light.colour * specularIntensity)
  }

  private def blocked(scene: Scene, self: Shape, ray: Ray): Boolean = {
    val possibleBlockers = scene.shapes.filterNot(_ == self)
    possibleBlockers.exists(_.intersects(ray).isDefined)
  }

  case class Ray(origin: Vector, direction: Vector) {
    def pointAt(t: Double): Vector = origin + (direction * t)
  }

  case class Intersection(point: Vector, distance: Double, obj: Shape)


}