package com.joe.raytrace

import com.joe.raytrace.Scene.{Camera, Light}
import spray.json._

object SceneProtocol extends DefaultJsonProtocol {

  def load(location: String): Scene = {
    import spray.json._
    val jsonSource = io.Source.fromInputStream(Scene.getClass.getResourceAsStream(location)).mkString
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
  implicit val LightFormat  = jsonFormat2(Light)
  implicit val CameraFormat = jsonFormat6(Camera)
  implicit val SceneFormat  = jsonFormat5(Scene.apply)

}