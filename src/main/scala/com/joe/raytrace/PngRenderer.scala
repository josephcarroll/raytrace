package com.joe.raytrace

import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.util.Base64
import javax.imageio.ImageIO

object PngRenderer {

  def generate(width: Int, height: Int, pixels: Array[Vector]): String = {
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

    val image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    for (y <- 0 until height; x <- 0 until width) {
      val i = x + (width * y)
      val Seq(r, g, b) = bytes(i)
      var rgb: Int = r
      rgb = (rgb << 8) + g
      rgb = (rgb << 8) + b
      image.setRGB(x, y, rgb)
    }

    val outputStream = new ByteArrayOutputStream()
    ImageIO.write(image, "png", outputStream)
    val pngByteArray = outputStream.toByteArray
    new String(Base64.getEncoder.encode(pngByteArray))
  }

}
