package com.joe.raytrace

import java.io.{File, FileOutputStream}

object FileRenderer {

  def renderToFile(width: Int, height: Int, pixels: Array[Vector]): Unit = {
    val bytes = pixels.flatMap { colour =>
      if (colour.x > 1.0 || colour.y > 1.0 || colour.z > 1.0) {
        // This is to point out errors where our final pixel is too bright!
        Seq(0.toByte, 255.toByte, 0.toByte)
      } else {
        val r = (255 * colour.x).toByte
        val g = (255 * colour.y).toByte
        val b = (255 * colour.z).toByte
        Seq(r, g, b)
      }
    }

    val f = new File("/Users/Joe/Desktop/result.ppm")
    if(!f.exists()) f.createNewFile()
    val fos = new FileOutputStream(f)
    fos.write(s"P6\n$width $height\n255\n".getBytes)
    fos.write(bytes.toArray)
    fos.close()
  }

}
