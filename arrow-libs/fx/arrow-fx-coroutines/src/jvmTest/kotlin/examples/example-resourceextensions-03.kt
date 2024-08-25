// This file was automatically generated from ResourceExtensions.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleResourceextensions03

import arrow.fx.coroutines.resourceScope
import arrow.fx.coroutines.singleThreadContext
import kotlinx.coroutines.withContext
import kotlinx.coroutines.ExecutorCoroutineDispatcher

suspend fun main(): Unit = resourceScope {
  val single: ExecutorCoroutineDispatcher = singleThreadContext("single")
  withContext(single) {
    println("I am running on ${Thread.currentThread().name}")
  }
}
