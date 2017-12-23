package com.joe.raytrace

case class Vector(x: Double, y: Double, z: Double) {

  def +(that: Vector): Vector = Vector(this.x + that.x, this.y + that.y, this.z + that.z)
  def -(that: Vector): Vector = Vector(this.x - that.x, this.y - that.y, this.z - that.z)
  def *(that: Vector): Vector = Vector(this.x * that.x, this.y * that.y, this.z * that.z)
  def *(constant: Double): Vector = Vector(this.x * constant, this.y * constant, this.z * constant)
  def /(constant: Double): Vector = Vector(this.x / constant, this.y / constant, this.z / constant)

  def length: Double = Math.sqrt((x * x) + (y * y) + (z * z))

  def cap(t: Double): Vector = Vector(Math.min(this.x, t), Math.min(this.y, t), Math.min(this.z, t))

  def dot(that: Vector): Double = {
    val m = this * that
    m.x + m.y + m.z
  }

  def normalize: Vector = {
    val normal = 1 / length
    Vector(x * normal, y * normal, z * normal)
  }

}

object Vector {

  val Zero = Vector(0.0, 0.0, 0.0)
  val One  = Vector(1.0, 1.0, 1.0)
  val Dark = Vector(0.1, 0.1, 0.1)

}