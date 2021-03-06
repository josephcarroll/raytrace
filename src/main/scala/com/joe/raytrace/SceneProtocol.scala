package com.joe.raytrace

import java.nio.file.Path

import com.joe.raytrace.Scene.{Camera, Light}
import spray.json._

import scala.io.Source

object SceneProtocol extends SceneProtocol

trait SceneProtocol extends DefaultJsonProtocol {

  implicit object VectorJsonFormat extends RootJsonFormat[Vector] {
    def write(c: Vector): JsArray = {
      JsArray(JsNumber(c.x), JsNumber(c.y), JsNumber(c.z))
    }

    def read(value: JsValue): Vector = value match {
      case JsArray(scala.collection.immutable.Vector(JsNumber(x), JsNumber(y), JsNumber(z))) =>
        Vector(x.doubleValue(), y.doubleValue(), z.doubleValue())
      case _ =>
        deserializationError("Vector expected")
    }
  }

  implicit val MaterialFormat: RootJsonFormat[Material] = jsonFormat5(Material)
  implicit val SphereFormat: RootJsonFormat[Sphere] = jsonFormat3(Sphere)
  implicit val PlaneFormat: RootJsonFormat[Plane] = jsonFormat3(Plane)
  implicit val LightFormat: RootJsonFormat[Light] = jsonFormat[Vector, Vector, Light](
    Light.apply, "position", "colour"
  )
  implicit val CameraFormat: RootJsonFormat[Camera] = jsonFormat3(Camera)
  implicit val SceneFormat: RootJsonFormat[Scene] = jsonFormat[Camera, Boolean, Vector, Vector, Seq[Light], Seq[Sphere], Seq[Plane], Scene](
    Scene.apply, "camera", "renderLights", "ambientLight", "backgroundColour", "lights", "spheres", "planes"
  )

  def load(path: Path): Scene = {
    val source = Source.fromFile(path.toFile)
    load(source)
  }

  def load(name: String): Scene = {
    val source = Source.fromInputStream(Scene.getClass.getResourceAsStream(s"/samples/$name.json"))
    load(source)
  }

  private def load(source: Source): Scene = {
    import spray.json._
    val jsonSource = source.mkString
    jsonSource.parseJson.convertTo[Scene]
  }

}