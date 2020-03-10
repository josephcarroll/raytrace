package com.joe.raytrace.sparse

import cern.colt.function.IntDoubleProcedure
import cern.colt.map.OpenIntDoubleHashMap
import org.apache.spark.mllib.linalg.SparseVector

import scala.collection.JavaConverters._

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
          if (currentValue != 0.0) {
            val newValue = results.get(currentIndex) + currentValue
            if (newValue != 0.0) {
              results.put(currentIndex, newValue)
            }
          }
        }
      }

      val indices = Array.fill(results.size)(0)
      val values = Array.fill(results.size)(0.0)
      var currentIndex = 0
      results.forEachPair(new IntDoubleProcedure {
        override def apply(i: Int, v: Double): Boolean = {
          if (v != 0.0) {
            indices(currentIndex) = i
            values(currentIndex) = v
            currentIndex += 1
          }
          true
        }
      })

      new SparseVector(resultLength, indices, values)
    }

  }

}
