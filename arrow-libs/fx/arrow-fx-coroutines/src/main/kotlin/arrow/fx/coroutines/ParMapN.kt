package arrow.fx.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Runs [fa], [fb] in parallel on [Dispatchers.Default] and combines their results using the provided function.
 *
 * ```kotlin:ank:playground
 * import arrow.fx.coroutines.*
 *
 * suspend fun main(): Unit {
 *   //sampleStart
 *   val result = parMapN(
 *     { "First one is on ${Thread.currentThread().name}" },
 *     { "Second one is on ${Thread.currentThread().name}" }
 *   ) { a, b ->
 *       "$a\n$b"
 *     }
 *   //sampleEnd
 *  println(result)
 * }
 * ```
 *
 * @param fa value to parallel map
 * @param fb value to parallel map
 * @param f function to map/combine value [A] and [B]
 *
 * @see parMapN for a function that can run on any [CoroutineContext]
 */
suspend inline fun <A, B, C> parMapN(
  crossinline fa: suspend () -> A,
  crossinline fb: suspend () -> B,
  crossinline f: suspend (A, B) -> C
): C = parMapN(Dispatchers.Default, fa, fb, f)

/**
 * Runs [fa], [fb] in parallel on [ctx] and combines their results using the provided function.
 *
 * Coroutine context is inherited from a [CoroutineScope], additional context elements can be specified with [ctx] argument.
 * If the combined context does not have any dispatcher nor any other [ContinuationInterceptor], then [Dispatchers.Default] is used.
 * **WARNING** If the combined context has a single threaded [ContinuationInterceptor], this function will not run [fa], [fb] in parallel.
 *
 * ```kotlin:ank:playground
 * import arrow.fx.coroutines.*
 * import kotlinx.coroutines.Dispatchers
 *
 * suspend fun main(): Unit {
 *   //sampleStart
 *   val result = parMapN(
 *     Dispatchers.IO,
 *     { "First one is on ${Thread.currentThread().name}" },
 *     { "Second one is on ${Thread.currentThread().name}" }
 *   ) { a, b ->
 *       "$a\n$b"
 *     }
 *   //sampleEnd
 *  println(result)
 * }
 * ```
 *
 * @param fa value to parallel map
 * @param fb value to parallel map
 * @param f function to map/combine value [A] and [B]
 *
 * @see parMapN for a function that ensures operations run in parallel on the [Dispatchers.Default].
 */
suspend inline fun <A, B, C> parMapN(
  ctx: CoroutineContext = EmptyCoroutineContext,
  crossinline fa: suspend () -> A,
  crossinline fb: suspend () -> B,
  crossinline f: suspend (A, B) -> C
): C = coroutineScope {
  val a = async(ctx) { fa() }
  val b = async(ctx) { fb() }
  f(a.await(), b.await())
}

/**
 * Runs [fa], [fb], [fc] in parallel on [Dispatchers.Default] and combines their results using the provided function.
 *
 * ```kotlin:ank:playground
 * import arrow.fx.coroutines.*
 *
 * suspend fun main(): Unit {
 *   //sampleStart
 *   val result = parMapN(
 *     { "First one is on ${Thread.currentThread().name}" },
 *     { "Second one is on ${Thread.currentThread().name}" },
 *     { "Third one is on ${Thread.currentThread().name}" }
 *   ) { a, b, c ->
 *       "$a\n$b\n$c"
 *     }
 *   //sampleEnd
 *  println(result)
 * }
 * ```
 *
 * @param fa value to parallel map
 * @param fb value to parallel map
 * @param fc value to parallel map
 * @param f function to map/combine value [A], [B] and [C]
 *
 * @see parMapN for a function that can run on any [CoroutineContext].
 */
suspend inline fun <A, B, C, D> parMapN(
  crossinline fa: suspend () -> A,
  crossinline fb: suspend () -> B,
  crossinline fc: suspend () -> C,
  crossinline f: suspend (A, B, C) -> D
): D = parMapN(Dispatchers.Default, fa, fb, fc, f)

/**
 * Runs [fa], [fb], [fc] in parallel on [ctx] and combines their results using the provided function.
 *
 * Coroutine context is inherited from a [CoroutineScope], additional context elements can be specified with [ctx] argument.
 * If the combined context does not have any dispatcher nor any other [ContinuationInterceptor], then [Dispatchers.Default] is used.
 * **WARNING** If the combined context has a single threaded [ContinuationInterceptor], this function will not run [fa], [fb] & [fc] in parallel.
 *
 * ```kotlin:ank:playground
 * import arrow.fx.coroutines.*
 * import kotlinx.coroutines.Dispatchers
 *
 * suspend fun main(): Unit {
 *   //sampleStart
 *   val result = parMapN(
 *     Dispatchers.IO,
 *     { "First one is on ${Thread.currentThread().name}" },
 *     { "Second one is on ${Thread.currentThread().name}" },
 *     { "Third one is on ${Thread.currentThread().name}" }
 *   ) { a, b, c ->
 *       "$a\n$b\n$c"
 *     }
 *   //sampleEnd
 *  println(result)
 * }
 * ```
 *
 * @param fa value to parallel map
 * @param fb value to parallel map
 * @param fc value to parallel map
 * @param f function to map/combine value [A], [B] and [C].
 *
 * @see parMapN for a function that ensures operations run in parallel on the [Dispatchers.Default].
 */
// TODO provide efficient impls like below for N-arity.
suspend inline fun <A, B, C, D> parMapN(
  ctx: CoroutineContext = EmptyCoroutineContext,
  crossinline fa: suspend () -> A,
  crossinline fb: suspend () -> B,
  crossinline fc: suspend () -> C,
  crossinline f: suspend (A, B, C) -> D
): D = coroutineScope {
  val a = async(ctx) { fa() }
  val b = async(ctx) { fb() }
  val c = async(ctx) { fc() }
  f(a.await(), b.await(), c.await())
}
