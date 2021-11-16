package arrow.fx.coroutines

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.Closeable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

/**
 * Creates a single threaded [CoroutineContext] as a [Resource].
 * Upon release an orderly shutdown of the [ExecutorService] takes place in which previously submitted
 * tasks are executed, but no new tasks will be accepted.
 *
 * ```kotlin:ank:playground
 * import arrow.fx.coroutines.*
 * import java.util.concurrent.Executors
 * import java.util.concurrent.atomic.AtomicInteger
 * import kotlin.math.max
 *
 * suspend fun main(): Unit {
 *   val pool = Resource.fromExecutor {
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
public fun Resource.Companion.fromExecutor(f: suspend () -> ExecutorService): Resource<CoroutineContext> =
  Resource(f) { s, _ -> s.shutdown() }.map(ExecutorService::asCoroutineDispatcher)

/**
 * Creates a [Resource] from an [Closeable], which uses [Closeable.close] for releasing.
 *
 * ```kotlin:ank:playground
 * import arrow.fx.coroutines.*
 * import java.io.FileInputStream
 *
 * suspend fun copyFile(src: String, dest: String): Unit =
 *   Resource.fromCloseable { FileInputStream(src) }
 *     .zip(Resource.fromCloseable { FileInputStream(dest) })
 *     .use { (a: FileInputStream, b: FileInputStream) ->
 *        /** read from [a] and write to [b]. **/
 *        // Both resources will be closed accordingly to their #close methods
 *     }
 * ```
 * <!--- KNIT example-resourceextensions-02.kt -->
 */
public fun <A : Closeable> Resource.Companion.fromCloseable(f: suspend () -> A): Resource<A> =
  Resource(f) { s, _ -> withContext(Dispatchers.IO) { s.close() } }

/**
 * Creates a [Resource] from an [AutoCloseable], which uses [AutoCloseable.close] for releasing.
 *
 * ```kotlin:ank:playground
 * import arrow.fx.coroutines.*
 * import java.io.FileInputStream
 *
 * suspend fun copyFile(src: String, dest: String): Unit =
 *   Resource.fromAutoCloseable { FileInputStream(src) }
 *     .zip(Resource.fromAutoCloseable { FileInputStream(dest) })
 *     .use { (a: FileInputStream, b: FileInputStream) ->
 *        /** read from [a] and write to [b]. **/
 *        // Both resources will be closed accordingly to their #close methods
 *     }
 * ```
 * <!--- KNIT example-resourceextensions-03.kt -->
 */
public fun <A : AutoCloseable> Resource.Companion.fromAutoCloseable(f: suspend () -> A): Resource<A> =
  Resource(f) { s, _ -> withContext(Dispatchers.IO) { s.close() } }

/**
 * Creates a single threaded [CoroutineContext] as a [Resource].
 * Upon release an orderly shutdown of the [ExecutorService] takes place in which previously submitted
 * tasks are executed, but no new tasks will be accepted.
 *
 * ```kotlin:ank:playground
 * import arrow.fx.coroutines.*
 * import kotlinx.coroutines.withContext
 *
 * val singleCtx = Resource.singleThreadContext("single")
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
public fun Resource.Companion.singleThreadContext(name: String): Resource<CoroutineContext> =
  fromExecutor {
    Executors.newSingleThreadExecutor { r ->
      Thread(r, name).apply {
        isDaemon = true
      }
    }
  }
