package com.joe.raytrace

import java.io.{FileOutputStream, File}

import  com.joe.raytrace.Tracer._

object FileRenderer {

  def renderToFile(width: Int, height: Int, pixels: Array[Vector]): Unit = {
    val bytes = pixels.flatMap { colour =>
      val r = (255 * colour.x).toByte
      val g = (255 * colour.y).toByte
      val b = (255 * colour.z).toByte
      Seq(r, g, b)
    }

    val f = new File("/Users/Joe/Desktop/result.ppm")
    if(!f.exists()) f.createNewFile()
    val fos = new FileOutputStream(f)
    fos.write(s"P6\n$width $height\n255\n".getBytes)
    fos.write(bytes.toArray)
    fos.close()
  }

}
