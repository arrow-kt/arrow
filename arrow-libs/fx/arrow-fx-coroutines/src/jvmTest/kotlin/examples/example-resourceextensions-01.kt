// This file was automatically generated from ResourceExtensions.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleResourceextensions01

import arrow.fx.coroutines.executor
import arrow.fx.coroutines.use
import arrow.fx.coroutines.parTraverse
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.max

suspend fun main(): Unit {
  resourceScope {
    val pool = executor {
      val ctr = AtomicInteger(0)
      val size = max(2, Runtime.getRuntime().availableProcessors())
      Executors.newFixedThreadPool(size) { r ->
        Thread(r, "computation-${ctr.getAndIncrement()}")
          .apply { isDaemon = true }
      }
    }

    listOf(1, 2, 3, 4, 5).parTraverse(ctx) { i ->
      println("#$i running on ${Thread.currentThread().name}")
    }
  }
}
