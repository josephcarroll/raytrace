package com.joe.raytrace

object Timer {

  def time[T](name: String)(func: => T): T= {
    val before = System.currentTimeMillis()
    val result = func
    val total = System.currentTimeMillis() - before
    println(s"$name completed in ${total}ms")
    result
  }

}
