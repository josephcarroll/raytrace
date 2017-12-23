package com.joe.raytrace.http

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import com.joe.raytrace.{PngRenderer, Renderer, Scene, SceneProtocol}

trait RenderService extends SprayJsonSupport with SceneProtocol {

  val route =
    pathPrefix("api") {
      path("render") {
        post {
          entity(as[Scene]) { scene =>
            parameters('width.as[Int], 'height.as[Int], 'antialiasing.as[Int]) { (width, height, antialiasing) =>
              complete {
                val rendered = Renderer.render(width, height, antialiasing)(scene)
                PngRenderer.generate(width, height, rendered)
              }
            }
          }
        }
      }
    } ~
      path("") {
        getFromResource("www/index.html")
      } ~ {
      getFromResourceDirectory("www")
    }
}