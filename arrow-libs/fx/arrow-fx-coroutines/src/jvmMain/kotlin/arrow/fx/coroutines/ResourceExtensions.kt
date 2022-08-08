package arrow.fx.coroutines

import arrow.fx.coroutines.ResourceDSL
import arrow.fx.coroutines.ResourceScope
import arrow.fx.coroutines.resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runInterruptible
import kotlinx.coroutines.withContext
import java.io.Closeable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration

@ResourceDSL
public suspend fun ResourceScope.executor(
  timeout: Duration = Duration.INFINITE,
  closingDispatcher: CoroutineDispatcher = Dispatchers.IO,
  create: suspend () -> ExecutorService,
): CoroutineContext = resource({ create() }) { s, _ ->
  s.shutdown()
  runInterruptible(closingDispatcher) {
    s.awaitTermination(timeout.inWholeMilliseconds, TimeUnit.MILLISECONDS)
  }
}.asCoroutineDispatcher()

/**
 * Creates a single threaded [CoroutineContext] as a [Resource].
 * Upon release an orderly shutdown of the [ExecutorService] takes place in which previously submitted
 * tasks are executed, but no new tasks will be accepted.
 *
 * ```kotlin
 * import arrow.fx.coroutines.executor
 * import arrow.fx.coroutines.use
 * import arrow.fx.coroutines.parTraverse
 * import java.util.concurrent.Executors
 * import java.util.concurrent.atomic.AtomicInteger
 * import kotlin.math.max
 *
 * suspend fun main(): Unit {
 *   val pool = executor {
 *     val ctr = AtomicInteger(0)
 *     val size = max(2, Runtime.getRuntime().availableProcessors())
 *     Executors.newFixedThreadPool(size) { r ->
 *       Thread(r, "computation-${ctr.getAndIncrement()}")
 *         .apply { isDaemon = true }
 *     }
 *   }
 *
 *   pool.use { ctx ->
 *     listOf(1, 2, 3, 4, 5).parTraverse(ctx) { i ->
 *       println("#$i running on ${Thread.currentThread().name}")
 *     }
 *   }
 * }
 * ```
 * <!--- KNIT example-resourceextensions-01.kt -->
 */
public fun executor(
  timeout: Duration = Duration.INFINITE,
  closingDispatcher: CoroutineDispatcher = Dispatchers.IO,
  create: suspend () -> ExecutorService,
): Resource<CoroutineContext> =
  resource {
    executor(timeout, closingDispatcher, create)
  }

/**
 * Creates a [Resource] from an [Closeable], which uses [Closeable.close] for releasing.
 *
 * ```kotlin
 * import arrow.fx.coroutines.resource
 * import arrow.fx.coroutines.closeable
 * import arrow.fx.coroutines.use
 * import java.io.FileInputStream
 *
 * suspend fun copyFile(src: String, dest: String): Unit =
 *   resource {
 *     val a: FileInputStream = closeable { FileInputStream(src) }
 *     val b: FileInputStream = closeable { FileInputStream(dest) }
 *     Pair(a, b)
 *   }.use { (a: FileInputStream, b: FileInputStream) ->
 *      /** read from [a] and write to [b]. **/
 *      // Both resources will be closed accordingly to their #close methods
 *   }
 * ```
 * <!--- KNIT example-resourceextensions-02.kt -->
 */
@ResourceDSL
public suspend fun <A : Closeable> ResourceScope.closeable(
  closingDispatcher: CoroutineDispatcher = Dispatchers.IO,
  closeable: suspend () -> A,
): A = resource(closeable) { s, _ -> withContext(closingDispatcher) { s.close() } }

public fun <A : Closeable> closeable(
  closingDispatcher: CoroutineDispatcher = Dispatchers.IO,
  closeable: suspend () -> A,
): Resource<A> = resource {
  closeable(closingDispatcher, closeable)
}


/**
 * Creates a [Resource] from an [AutoCloseable], which uses [AutoCloseable.close] for releasing.
 *
 * ```kotlin
 * import arrow.fx.coroutines.resource
 * import arrow.fx.coroutines.autoCloseable
 * import arrow.fx.coroutines.use
 * import java.io.FileInputStream
 *
 * suspend fun copyFile(src: String, dest: String): Unit =
 *   resource {
 *     val a: FileInputStream = autoCloseable { FileInputStream(src) }
 *     val b: FileInputStream = autoCloseable { FileInputStream(dest) }
 *     Pair(a, b)
 *   }.use { (a: FileInputStream, b: FileInputStream) ->
 *      /** read from [a] and write to [b]. **/
 *      // Both resources will be closed accordingly to their #close methods
 *   }
 * ```
 * <!--- KNIT example-resourceextensions-03.kt -->
 */
@ResourceDSL
public suspend fun <A : AutoCloseable> ResourceScope.autoCloseable(
  closingDispatcher: CoroutineDispatcher = Dispatchers.IO,
  autoCloseable: suspend () -> A,
): A = resource(autoCloseable) { s, _ -> withContext(closingDispatcher) { s.close() } }

public fun <A : AutoCloseable> autoCloseable(
  closingDispatcher: CoroutineDispatcher = Dispatchers.IO,
  autoCloseable: suspend () -> A,
): Resource<A> = resource {
  autoCloseable(closingDispatcher, autoCloseable)
}

@ResourceDSL
public suspend fun ResourceScope.singleThreadContext(name: String): CoroutineContext =
  executor {
    Executors.newSingleThreadExecutor { r ->
      Thread(r, name).apply {
        isDaemon = true
      }
    }
  }

/**
 * Creates a single threaded [CoroutineContext] as a [Resource].
 * Upon release an orderly shutdown of the [ExecutorService] takes place in which previously submitted
 * tasks are executed, but no new tasks will be accepted.
 *
 * ```kotlin
 * import arrow.fx.coroutines.singleThreadContext
 * import arrow.fx.coroutines.use
 * import kotlinx.coroutines.withContext
 *
 * val singleCtx = singleThreadContext("single")
 *
 * suspend fun main(): Unit =
 *   singleCtx.use { ctx ->
 *     withContext(ctx) {
 *       println("I am running on ${Thread.currentThread().name}")
 *     }
 *   }
 * ```
 * <!--- KNIT example-resourceextensions-04.kt -->
 */
public fun singleThreadContext(name: String): Resource<CoroutineContext> =
  resource { singleThreadContext(name) }
