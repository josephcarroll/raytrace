package com.joe.raytrace.benchmark

import com.joe.raytrace.{Renderer, SceneProtocol}

object SimpleBenchmark extends App with SceneProtocol {

  val iterations = 10
  val antialiasing = 2
  val pixelWidth   = 300
  val pixelHeight  = 300
  val scene = SceneProtocol.load("billiards")

  val timings = for (i <- 1 to iterations) yield {
    val before = System.currentTimeMillis()
    Renderer.render(pixelWidth, pixelHeight, antialiasing)(scene)
    System.currentTimeMillis() - before
  }

  println()
  println("-------------------------------------")
  println(s"avg: ${timings.sum / timings.length}ms, min: ${timings.min}ms, max: ${timings.max}ms")
  println("-------------------------------------")

}
