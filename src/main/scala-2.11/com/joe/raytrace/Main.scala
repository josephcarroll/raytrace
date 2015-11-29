package com.joe.raytrace

import Timer._

object Main extends App {

  val (Array(widthInput, heightInput, antialiasingInput), inputs) = args.splitAt(3)
  val antialiasing = antialiasingInput.toInt
  val pixelWidth   = heightInput.toInt
  val pixelHeight  = widthInput.toInt

  time("All processing") {
    inputs.foreach { name =>
      val scene = SceneProtocol.load(name)
      val rendered = time(name + " rendering") {
        Renderer.render(pixelWidth, pixelHeight, antialiasing)(scene)
      }
      FileRenderer.renderToFile(pixelWidth, pixelHeight, rendered, name)
    }
  }

}
