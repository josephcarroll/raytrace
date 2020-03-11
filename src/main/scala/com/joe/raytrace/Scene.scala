package com.joe.raytrace

import com.joe.raytrace.Scene.{Camera, Light}
import com.joe.raytrace.Tracer.Ray

case class Scene(camera: Camera, renderLights: Boolean, ambientLight: Vector, backgroundColour: Vector, lights: Seq[Light], spheres: Seq[Sphere], planes: Seq[Plane]) {
  val lightShapes: Seq[Light] = if (renderLights) lights else Nil
  val shapes: Array[Shape] = (spheres ++ planes ++ lightShapes).toArray
}

object Scene {

  case class Camera(origin: Vector, direction: Vector, fov: Int)

  case class Light(position: Vector, colour: Vector) extends Shape {
    private val underlyingSphere = Sphere(position, 0.1, Material(Vector.One, Vector.One, 0.0, checkered = false, 0.0))
    override def intersects(ray: Ray): Double = underlyingSphere.intersects(ray)
    override def normal(point: Vector): Vector = underlyingSphere.normal(point)
    override def material: Material = underlyingSphere.material
    override def castsShadow: Boolean = false
  }

}