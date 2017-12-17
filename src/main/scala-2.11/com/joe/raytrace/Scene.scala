package com.joe.raytrace

import com.joe.raytrace.Scene.{Camera, Light}
import com.joe.raytrace.Tracer.{Intersection, Ray}

case class Scene(camera: Camera, ambientLight: Vector, backgroundColour: Vector, lights: Seq[Light], spheres: Seq[Sphere], planes: Seq[Plane]) {
  val shapes: Seq[Shape] = spheres ++ planes ++ lights
}

object Scene {

  case class Camera(origin: Vector, direction: Vector, fov: Int)

  case class Light(position: Vector, colour: Vector) extends Shape {
    private val underlyingSphere = Sphere(position, 0.3, Material(Vector.One, Vector.One, 0.0))
    override def intersects(ray: Ray): Option[Intersection] = underlyingSphere.intersects(ray)
    override def normal(point: Vector): Vector = underlyingSphere.normal(point)
    override def material: Material = underlyingSphere.material
    override def castsShadow: Boolean = false
  }

}