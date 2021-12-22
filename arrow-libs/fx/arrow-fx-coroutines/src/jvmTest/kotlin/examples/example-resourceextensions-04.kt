// This file was automatically generated from ResourceExtensions.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleResourceextensions04

import arrow.fx.coroutines.*
import kotlinx.coroutines.withContext

val singleCtx = Resource.singleThreadContext("single")

suspend fun main(): Unit =
  singleCtx.use { ctx ->
    withContext(ctx) {
      println("I am running on ${Thread.currentThread().name}")
    }
  }
