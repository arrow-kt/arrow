// This file was automatically generated from ResourceExtensions.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleResourceextensions01

import arrow.fx.coroutines.*
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.max

suspend fun main(): Unit {
  val pool = Resource.fromExecutor {
    val ctr = AtomicInteger(0)
    val size = max(2, Runtime.getRuntime().availableProcessors())
    Executors.newFixedThreadPool(size) { r ->
      Thread(r, "computation-${ctr.getAndIncrement()}")
        .apply { isDaemon = true }
    }
  }

  pool.use { ctx ->
    listOf(1, 2, 3, 4, 5).parTraverse(ctx) { i ->
      println("#$i running on ${Thread.currentThread().name}")
    }
  }
}
