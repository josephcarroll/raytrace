package com.joe.raytrace.http

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import com.joe.raytrace.{Renderer, Scene, SceneProtocol}

trait RenderService extends SprayJsonSupport with SceneProtocol {

  val route =
    pathPrefix("api") {
      path("render") {
        post {
          entity(as[Scene]) { scene =>
            parameters('width.as[Int], 'height.as[Int], 'antialiasing.as[Int]) { (width, height, antialiasing) =>
              complete {
                Renderer.render(width, height, antialiasing)(scene)
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