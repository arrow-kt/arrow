// This file was automatically generated from ResourceExtensions.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleResourceextensions04

import arrow.fx.coroutines.fixedThreadPoolContext
import arrow.fx.coroutines.resourceScope
import kotlinx.coroutines.withContext
import kotlinx.coroutines.ExecutorCoroutineDispatcher

suspend fun main(): Unit = resourceScope {
  val pool: ExecutorCoroutineDispatcher = fixedThreadPoolContext(8, "custom-pool")
  withContext(pool) {
    println("I am running on ${Thread.currentThread().name}")
  }
}
