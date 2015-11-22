package com.joe.raytrace

import com.joe.raytrace.Scene.{Camera, Light}
import spray.json._

object SceneProtocol extends DefaultJsonProtocol {

  def load(name: String): Scene = {
    import spray.json._
    val jsonSource = io.Source.fromInputStream(Scene.getClass.getResourceAsStream(s"scenes/$name.json")).mkString
    jsonSource.parseJson.convertTo[Scene]
  }

  implicit object VectorJsonFormat extends RootJsonFormat[Vector] {
    def write(c: Vector) = throw new UnsupportedOperationException
    def read(value: JsValue) = value match {
      case JsArray(scala.collection.immutable.Vector(JsNumber(x), JsNumber(y), JsNumber(z))) =>
        Vector(x.doubleValue(), y.doubleValue(), z.doubleValue())
      case _ =>
        deserializationError("Vector expected")
    }
  }

  implicit val SphereFormat = jsonFormat3(Sphere)
  implicit val PlaneFormat  = jsonFormat3(Plane)
  implicit val LightFormat  = jsonFormat2(Light)
  implicit val CameraFormat = jsonFormat5(Camera)
  implicit val SceneFormat  = jsonFormat[Camera, Vector, Vector, Seq[Light], Seq[Sphere], Seq[Plane], Scene](
    Scene.apply, "camera", "ambientLight", "backgroundColour", "lights", "spheres", "planes"
  )
}