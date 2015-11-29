package com.joe.raytrace

import Timer._

object Main extends App {

  val (Array(widthInput, heightInput, antialiasingInput), inputs) = args.splitAt(3)
  val antialiasing = antialiasingInput.toInt
  val pixelWidth   = widthInput.toInt
  val pixelHeight  = heightInput.toInt

  time("All processing") {
    inputs.map(SceneProtocol.load).foreach(Renderer.render(pixelWidth, pixelHeight, antialiasing))
  }

}
