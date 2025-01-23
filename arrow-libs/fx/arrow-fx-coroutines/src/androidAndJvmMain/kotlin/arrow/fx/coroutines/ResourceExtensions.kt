package arrow.fx.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runInterruptible
import kotlinx.coroutines.withContext
import java.io.Closeable
import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration

internal actual val IODispatcher: CoroutineDispatcher = Dispatchers.IO

/**
 * Creates a single threaded [CoroutineContext] as a [Resource].
 * Upon release an orderly shutdown of the [ExecutorService] takes place in which previously submitted
 * tasks are executed, but no new tasks will be accepted.
 *
 * ```kotlin
 * import arrow.fx.coroutines.executor
 * import arrow.fx.coroutines.resourceScope
 * import arrow.fx.coroutines.parMap
 * import java.util.concurrent.Executors
 * import java.util.concurrent.atomic.AtomicInteger
 * import kotlin.math.max
 *
 * suspend fun main(): Unit {
 *   resourceScope {
 *     val pool = executor {
 *       val ctr = AtomicInteger(0)
 *       val size = max(2, Runtime.getRuntime().availableProcessors())
 *       Executors.newFixedThreadPool(size) { r ->
 *         Thread(r, "computation-${ctr.getAndIncrement()}")
 *           .apply { isDaemon = true }
 *       }
 *     }
 *
 *     listOf(1, 2, 3, 4, 5).parMap(pool) { i ->
 *       println("#$i running on ${Thread.currentThread().name}")
 *     }
 *   }
 * }
 * ```
 * <!--- KNIT example-resourceextensions-01.kt -->
 */
@ResourceDSL
public suspend fun ResourceScope.executor(
  timeout: Duration = Duration.INFINITE,
  closingDispatcher: CoroutineDispatcher = Dispatchers.IO,
  create: suspend () -> ExecutorService,
): ExecutorCoroutineDispatcher = install({ create() } ) { s: ExecutorService, _: ExitCase ->
  s.shutdown()
  runInterruptible(closingDispatcher) {
    s.awaitTermination(timeout.inWholeMilliseconds, TimeUnit.MILLISECONDS)
  }
}.asCoroutineDispatcher()

public fun executor(
  timeout: Duration = Duration.INFINITE,
  closingDispatcher: CoroutineDispatcher = Dispatchers.IO,
  create: suspend () -> ExecutorService,
): Resource<ExecutorCoroutineDispatcher> =
  resource {
    executor(timeout, closingDispatcher, create)
  }

/**
 * Creates a [Resource] from an [Closeable], which uses [Closeable.close] for releasing.
 *
 * ```kotlin
 * import arrow.fx.coroutines.resourceScope
 * import arrow.fx.coroutines.closeable
 * import java.io.FileInputStream
 *
 * suspend fun copyFile(src: String, dest: String): Unit =
 *   resourceScope {
 *     val a: FileInputStream = closeable { FileInputStream(src) }
 *     val b: FileInputStream = closeable { FileInputStream(dest) }
 *     /** read from `a` and write to `b`. **/
 *   } // Both resources will be closed accordingly to their #close methods
 * ```
 * <!--- KNIT example-resourceextensions-02.kt -->
 */
@ResourceDSL
public suspend fun <A : Closeable> ResourceScope.closeable(
  closingDispatcher: CoroutineDispatcher = Dispatchers.IO,
  closeable: suspend () -> A,
): A = install({ closeable() } ) { s: A, _: ExitCase -> withContext(closingDispatcher) { s.close() } }

public fun <A : Closeable> closeable(
  closingDispatcher: CoroutineDispatcher = Dispatchers.IO,
  closeable: suspend () -> A,
): Resource<A> = resource {
  closeable(closingDispatcher, closeable)
}

/**
 * Creates a single threaded [CoroutineContext] as a [Resource].
 * Upon release an orderly shutdown of the [ExecutorService] takes place in which previously submitted
 * tasks are executed, but no new tasks will be accepted.
 *
 * ```kotlin
 * import arrow.fx.coroutines.resourceScope
 * import arrow.fx.coroutines.singleThreadContext
 * import kotlinx.coroutines.withContext
 * import kotlinx.coroutines.ExecutorCoroutineDispatcher
 *
 * suspend fun main(): Unit = resourceScope {
 *   val single: ExecutorCoroutineDispatcher = singleThreadContext("single")
 *   withContext(single) {
 *     println("I am running on ${Thread.currentThread().name}")
 *   }
 * }
 * ```
 * ```text
 * I am running on single
 * ```
 * <!--- KNIT example-resourceextensions-03.kt -->
 */
@OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
@ResourceDSL
public suspend fun ResourceScope.singleThreadContext(name: String): ExecutorCoroutineDispatcher =
  closeable { newSingleThreadContext(name) }

public fun singleThreadContext(name: String): Resource<ExecutorCoroutineDispatcher> =
  resource { singleThreadContext(name) }

/**
 * Creates a single threaded [CoroutineContext] as a [Resource].
 * Upon release an orderly shutdown of the [ExecutorService] takes place in which previously submitted
 * tasks are executed, but no new tasks will be accepted.
 *
 * ```kotlin
 * import arrow.fx.coroutines.fixedThreadPoolContext
 * import arrow.fx.coroutines.resourceScope
 * import kotlinx.coroutines.withContext
 * import kotlinx.coroutines.ExecutorCoroutineDispatcher
 *
 * suspend fun main(): Unit = resourceScope {
 *   val pool: ExecutorCoroutineDispatcher = fixedThreadPoolContext(8, "custom-pool")
 *   withContext(pool) {
 *     println("I am running on ${Thread.currentThread().name}")
 *   }
 * }
 * ```
 * ```text
 * I am running on custom-pool-1
 * ```
 * <!--- KNIT example-resourceextensions-04.kt -->
 */
@OptIn(DelicateCoroutinesApi::class)
@ResourceDSL
public suspend fun ResourceScope.fixedThreadPoolContext(nThreads: Int, name: String): ExecutorCoroutineDispatcher =
  closeable { newFixedThreadPoolContext(nThreads, name) }

public fun fixedThreadPoolContext(nThreads: Int, name: String): Resource<ExecutorCoroutineDispatcher> =
  resource { fixedThreadPoolContext(nThreads, name) }
