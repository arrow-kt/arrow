//[arrow-fx-coroutines](../../index.md)/[arrow.fx.coroutines](index.md)/[fromExecutor](from-executor.md)

# fromExecutor

[jvm]\
fun [Resource.Companion](-resource/-companion/index.md#-1559173624%2FExtensions%2F1399459356).[fromExecutor](from-executor.md)(f: suspend () -&gt; [ExecutorService](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ExecutorService.html)): [Resource](../../../arrow-fx-coroutines/arrow-fx-coroutines/arrow.fx.coroutines/-resource/index.md)&lt;[CoroutineContext](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html)&gt;

Creates a single threaded [CoroutineContext](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html) as a [Resource](../../../arrow-fx-coroutines/arrow-fx-coroutines/arrow.fx.coroutines/-resource/index.md). Upon release an orderly shutdown of the [ExecutorService](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ExecutorService.html) takes place in which previously submitted tasks are executed, but no new tasks will be accepted.

import arrow.fx.coroutines.*\
import java.util.concurrent.Executors\
import java.util.concurrent.atomic.AtomicInteger\
import kotlin.math.max\
\
suspend fun main(): Unit {\
  val pool = Resource.fromExecutor {\
    val ctr = AtomicInteger(0)\
    val size = max(2, Runtime.getRuntime().availableProcessors())\
    Executors.newFixedThreadPool(size) { r -&gt;\
      Thread(r, "computation-${ctr.getAndIncrement()}")\
        .apply { isDaemon = true }\
    }\
  }\
\
  pool.use { ctx -&gt;\
    listOf(1, 2, 3, 4, 5).parTraverse(ctx) { i -&gt;\
      println("#$i running on ${Thread.currentThread().name}")\
    }\
  }\
}<!--- KNIT example-resourceextensions-01.kt -->
