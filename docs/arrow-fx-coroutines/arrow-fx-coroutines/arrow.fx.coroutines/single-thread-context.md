//[arrow-fx-coroutines](../../index.md)/[arrow.fx.coroutines](index.md)/[singleThreadContext](single-thread-context.md)

# singleThreadContext

[jvm]\
fun [Resource.Companion](-resource/-companion/index.md#-1559173624%2FExtensions%2F1399459356).[singleThreadContext](single-thread-context.md)(name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [Resource](../../../arrow-fx-coroutines/arrow-fx-coroutines/arrow.fx.coroutines/-resource/index.md)&lt;[CoroutineContext](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html)&gt;

Creates a single threaded [CoroutineContext](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html) as a [Resource](../../../arrow-fx-coroutines/arrow-fx-coroutines/arrow.fx.coroutines/-resource/index.md). Upon release an orderly shutdown of the [ExecutorService](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ExecutorService.html) takes place in which previously submitted tasks are executed, but no new tasks will be accepted.

import arrow.fx.coroutines.*\
import kotlinx.coroutines.withContext\
\
val singleCtx = Resource.singleThreadContext("single")\
\
suspend fun main(): Unit =\
  singleCtx.use { ctx -&gt;\
    withContext(ctx) {\
      println("I am running on ${Thread.currentThread().name}")\
    }\
  }<!--- KNIT example-resourceextensions-04.kt -->
