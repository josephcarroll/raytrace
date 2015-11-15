package com.joe.raytrace

import com.joe.raytrace.Geometry.Sphere
import com.joe.raytrace.Scene.{Camera, Light}
import com.joe.raytrace.Tracer._

case class Scene(camera: Camera, backgroundColour: Vector, lights: Seq[Light], spheres: Seq[Sphere])

object Scene {

  case class Camera(origin: Vector, direction: Vector, pixelWidth: Int, pixelHeight: Int, antialiasing: Int, fieldOfView: Double) {

    def width  = pixelWidth * antialiasing
    def height = pixelHeight * antialiasing
    def samplesPerPixel = Math.pow(antialiasing, 2)
    def aspectRatio = width / height.toDouble

  }

  case class Light(position: Vector, colour: Vector)

}