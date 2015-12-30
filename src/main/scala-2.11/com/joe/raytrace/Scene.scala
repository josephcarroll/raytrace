package com.joe.raytrace

import com.joe.raytrace.Scene.{Camera, Light}

case class Scene(camera: Camera, ambientLight: Vector, backgroundColour: Vector, lights: Seq[Light], spheres: Seq[Sphere], planes: Seq[Plane]) {
  val shapes = spheres ++ planes
}

object Scene {

  case class Camera(origin: Vector, direction: Vector, fov: Int)

  case class Light(position: Vector, colour: Vector)

}