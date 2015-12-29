package com.joe.raytrace

import java.io.File
import java.nio.file.Path

import Timer._

import scala.io.StdIn

object Main extends App {

  val Array(widthInput, heightInput, antialiasingInput) = args
  val antialiasing = antialiasingInput.toInt
  val pixelWidth   = heightInput.toInt
  val pixelHeight  = widthInput.toInt

  while(true) {
    val source = StdIn.readLine("Source: ")
    val path = new File(source).toPath.toAbsolutePath
    process(path)
  }

  def process(path: Path): Unit = {
    val scene = SceneProtocol.load(path)
    val name = path.toString
    val rendered = time(s"$name rendering") {
      Renderer.render(pixelWidth, pixelHeight, antialiasing)(scene)
    }
    FileRenderer.renderToPath(pixelWidth, pixelHeight, rendered, path.resolveSibling(path.getFileName.toString + ".png"))
  }

}
