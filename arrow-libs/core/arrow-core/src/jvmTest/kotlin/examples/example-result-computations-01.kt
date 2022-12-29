// This file was automatically generated from result.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleResultComputations01

import arrow.core.*
import arrow.core.computations.ResultEffect.result

fun main() {
  result { // We can safely use assertion based operation inside blocks
    kotlin.require(false) { "Boom" }
  } // Result.Failure<Int>(IllegalArgumentException("Boom"))

  result {
    Result.failure<Int>(RuntimeException("Boom"))
      .recover { 1 }
      .bind()
  } // Result.Success(1)

  result {
    val x = Result.success(1).bind()
    val y = Result.success(x + 1).bind()
    x + y
  } // Result.Success(3)
}
