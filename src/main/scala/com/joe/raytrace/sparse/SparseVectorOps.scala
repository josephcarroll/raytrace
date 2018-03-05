package com.joe.raytrace.sparse

import cern.colt.function.IntDoubleProcedure
import cern.colt.map.OpenIntDoubleHashMap
import org.apache.spark.mllib.linalg.SparseVector

import scala.collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer

object SparseVectorOps {

  implicit class SparseVectorMethods(vector: SparseVector) {

    def convolve(kernel: Array[Double]): SparseVector = {
      val kernelWithIndex = kernel.zipWithIndex

      val results = new java.util.HashMap[Int, Double]
      val resultLength = vector.size + kernel.length - 1

      vector.foreachActive { (vectorIndex, vectorValue) =>
        kernelWithIndex.foreach { case (kernelValue, kernelIndex) =>
          val currentValue = kernelValue * vectorValue
          val currentIndex = vectorIndex + kernelIndex
          results.put(currentIndex, results.getOrDefault(currentIndex, 0.0) + currentValue)
        }
      }

      val (indices, values) = results.asScala.toArray.unzip
      new SparseVector(resultLength, indices, values)
    }

    def convolveFast(kernel: Array[Double]): SparseVector = {
      val kernelWithIndex = kernel.zipWithIndex

      val results = new OpenIntDoubleHashMap()
      val resultLength = vector.size + kernel.length - 1

      vector.foreachActive { (vectorIndex, vectorValue) =>
        kernelWithIndex.foreach { case (kernelValue, kernelIndex) =>
          val currentValue = kernelValue * vectorValue
          val currentIndex = vectorIndex + kernelIndex
          results.put(currentIndex, results.get(currentIndex) + currentValue)
        }
      }

      val indices = new ArrayBuffer[Int](results.size)
      val values = new ArrayBuffer[Double](results.size)
      results.forEachPair(new IntDoubleProcedure {
        override def apply(i: Int, v: Double): Boolean = {
          if (v != 0.0) {
            indices += i
            values += v
          }
          true
        }
      })

      new SparseVector(resultLength, indices.toArray, values.toArray)
    }

  }

}
