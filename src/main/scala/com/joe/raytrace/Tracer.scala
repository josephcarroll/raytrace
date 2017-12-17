package com.joe.raytrace

import com.joe.raytrace.Scene.Light

object Tracer {

  def traceRay(scene: Scene)(ray: Ray): Vector = {
    minIntersectionOf(ray, scene.shapes) match {
      case Some(intersection) =>
        val lightInput = scene.lights.map(colourFromLight(intersection, scene, ray))
        val lightSum = lightInput.foldLeft(Vector.Zero)(_ + _)
        val emissionLight = intersection.obj.material.emissionColour
        (scene.ambientLight + lightSum + emissionLight).cap(1.0)
      case None =>
        scene.backgroundColour
    }
  }

  private def colourFromLight(intersection: Intersection, scene: Scene, viewerRay: Ray)(light: Light): Vector = {
    val normal = intersection.obj.normal(intersection.point)
    val lightRay = (light.position - intersection.point).normalize

    val shininess = intersection.obj.material.shininess
    val specularIntensity = if (shininess > 0) {
      val reflectedRay = lightRay - (normal * 2.0 * normal.dot(lightRay)) // l - 2(n.l)n
      val shininessPower = shininess * 32.0
      Math.pow(Math.max(0.0, viewerRay.direction.dot(reflectedRay)), shininessPower)
    } else 0.0

    val inShade = blocked(scene, intersection.obj, Ray(intersection.point, lightRay))
    if(inShade) {
      Vector.Zero
    } else {
      val diffuseIntensity = Math.max(0.0, lightRay.dot(normal))
      (intersection.obj.material.diffuseColour * light.colour * diffuseIntensity) + (light.colour * specularIntensity)
    }
  }

  private def blocked(scene: Scene, self: Shape, ray: Ray): Boolean = {
    scene.shapes.exists(shape => shape != self && shape.castsShadow && shape.intersects(ray) != Double.MaxValue)
  }

  private def minIntersectionOf(ray: Ray, shapes: Array[Shape]): Option[Intersection] = {
    var minIntersectionDistance = Double.MaxValue
    var minIntersectionShape: Shape = null

    for (shape <- shapes) {
      val intersectionLength = shape.intersects(ray)
      if (intersectionLength < Double.MaxValue) {
        val distanceFromOrigin = (ray.pointAt(intersectionLength) - ray.origin).length
        if (distanceFromOrigin < minIntersectionDistance) {
          minIntersectionDistance = distanceFromOrigin
          minIntersectionShape = shape
        }
      }
    }

    if (minIntersectionShape == null) None else Some(Intersection(ray, minIntersectionDistance, minIntersectionShape))
  }

  case class Ray(origin: Vector, direction: Vector) {
    def pointAt(t: Double): Vector = origin + (direction * t)
  }

  private case class Intersection(ray: Ray, distance: Double, obj: Shape) {
    val point = ray.pointAt(distance)
  }


}