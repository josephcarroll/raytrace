package com.joe.raytrace.sparse

import org.apache.commons.math3.util.MathArrays
import org.scalatest.{FlatSpec, Matchers}
import SparseVectorOps._
import org.apache.spark.mllib.linalg.SparseVector

import scala.collection.mutable.ArrayBuffer

class SparseVectorOpsSpec extends FlatSpec with Matchers {

  "SparseVectorOps" should "convolve a simple vector" in {
    check(Array(1.0, 1.0, 1.0, 1.0), Array(1.0, 1.0, 1.0))
    check(Array(2.4, 4.5, 1.2, 5.2), Array(0.4, 4.5))
  }

  it should "handle a single element kernel" in {
    check(Array(1.0, 1.0, 1.0, 1.0), Array(1.0))
  }

  it should "convolve a very sparse vector" in {
    check(Array(0.0, 1.0, 0.0, 0.0), Array(0.0, 0.0, 0.0, 1.0, 1.0, 1.0))
  }

  it should "convolve a very large array" in {
    val large = Array.fill(100000)(0.0) ++ Array(1.0) ++ Array.fill(100000)(0.0) ++ Array(1.0)
    check(large, Array.fill(3000)(0.0) ++ Array.fill(3000)(1.0))
  }

  it should "convolve a large array with random values" in {
    val indices =  for (_ <- 1 to (21000000 * 0.01).toInt) yield {
      (Math.random() * 21000000).toInt
    }

    val sparse = new SparseVector(21000000, indices.toArray, Array.fill(indices.length)(1.0))
    val convolved = sparse.convolveFast(Array.fill(3000)(0.0) ++ Array.fill(3000)(1.0))
    convolved.size shouldEqual (21000000 + 6000 - 1)
  }

  private def check(input: Array[Double], kernel: Array[Double]): Unit = {
    val expectedResult = MathArrays.convolve(input, kernel)
    val inputAsSparse = asSparse(input)
    val actualResultFast = inputAsSparse.convolveFast(kernel).toArray
    actualResultFast shouldEqual expectedResult
  }

  private def asSparse(input: Array[Double]): SparseVector = {
    val indices = new ArrayBuffer[Int]
    val values = new ArrayBuffer[Double]
    input.zipWithIndex.foreach { case (v, i) =>
      if (v != 0.0) {
        indices += i
        values += v
      }
    }
    new SparseVector(input.length, indices.toArray, values.toArray)
  }

}
