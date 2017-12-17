package com.joe.raytrace.http

import akka.actor.Actor
import com.joe.raytrace.{SceneProtocol, Renderer, Scene}
import spray.httpx.SprayJsonSupport
import spray.routing.HttpService

class RenderServiceActor extends Actor with RenderService {

  def actorRefFactory = context
  def receive = runRoute(route)

}

trait RenderService extends HttpService with SprayJsonSupport with SceneProtocol {

  val route =
    compressResponseIfRequested() {
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
}