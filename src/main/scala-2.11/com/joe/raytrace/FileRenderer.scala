package com.joe.raytrace

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

object FileRenderer {

  def renderToFile(width: Int, height: Int, pixels: Array[Vector], name: String): Unit = {
    val bytes = pixels.map { colour =>
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

    val image = new BufferedImage(height, width, BufferedImage.TYPE_INT_RGB)
    for (y <- 0 until height; x <- 0 until width) {
      val i = x + (width * y)
      val Seq(r, g, b) = bytes(i)
      var rgb: Int = r
      rgb = (rgb << 8) + g
      rgb = (rgb << 8) + b
      image.setRGB(y, x, rgb)
    }
    val f = new File(s"/Users/Joe/Desktop/Renders/$name.png")
    if(!f.exists()) {
      f.mkdirs()
      f.createNewFile()
    }
    ImageIO.write(image, "png", f)
  }

}
