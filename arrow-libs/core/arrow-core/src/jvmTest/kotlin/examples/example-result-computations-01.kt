// This file was automatically generated from result.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleResultComputations01

import arrow.*
import arrow.core.*
import arrow.core.coroutines.*
import kotlinx.coroutines.*
import io.kotest.matchers.collections.*
import io.kotest.assertions.*
import io.kotest.matchers.*
import io.kotest.matchers.types.*
import kotlin.coroutines.cancellation.CancellationException
import io.kotest.property.*
import io.kotest.property.arbitrary.*
import arrow.core.test.generators.*

import arrow.core.*
import arrow.core.computations.result

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
