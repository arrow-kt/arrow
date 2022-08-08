// This file was automatically generated from ResourceExtensions.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleResourceextensions04

import arrow.fx.coroutines.singleThreadContext
import arrow.fx.coroutines.use
import kotlinx.coroutines.withContext

val singleCtx = singleThreadContext("single")

suspend fun main(): Unit =
  singleCtx.use { ctx ->
    withContext(ctx) {
      println("I am running on ${Thread.currentThread().name}")
    }
  }
