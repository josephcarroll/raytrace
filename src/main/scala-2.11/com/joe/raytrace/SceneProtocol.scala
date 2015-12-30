package com.joe.raytrace

import java.nio.file.Path

import com.joe.raytrace.Scene.{Camera, Light}
import spray.json._

import scala.io.Source

object SceneProtocol extends SceneProtocol

trait SceneProtocol extends DefaultJsonProtocol {

  def load(path: Path): Scene = {
    val source = io.Source.fromFile(path.toFile)
    load(source)
  }

  def load(name: String): Scene = {
    val source = io.Source.fromInputStream(Scene.getClass.getResourceAsStream(s"scenes/$name.json"))
    load(source)
  }

  private def load(source: Source): Scene = {
    import spray.json._
    val jsonSource = source.mkString
    jsonSource.parseJson.convertTo[Scene]
  }

  implicit object VectorJsonFormat extends RootJsonFormat[Vector] {
    def write(c: Vector) = {
      JsArray(JsNumber(c.x), JsNumber(c.y), JsNumber(c.z))
    }

    def read(value: JsValue) = value match {
      case JsArray(scala.collection.immutable.Vector(JsNumber(x), JsNumber(y), JsNumber(z))) =>
        Vector(x.doubleValue(), y.doubleValue(), z.doubleValue())
      case _ =>
        deserializationError("Vector expected")
    }
  }

  implicit val MaterialFormat = jsonFormat2(Material)
  implicit val SphereFormat   = jsonFormat3(Sphere)
  implicit val PlaneFormat    = jsonFormat3(Plane)
  implicit val LightFormat    = jsonFormat2(Light)
  implicit val CameraFormat   = jsonFormat3(Camera)
  implicit val SceneFormat    = jsonFormat[Camera, Vector, Vector, Seq[Light], Seq[Sphere], Seq[Plane], Scene](
    Scene.apply, "camera", "ambientLight", "backgroundColour", "lights", "spheres", "planes"
  )
}