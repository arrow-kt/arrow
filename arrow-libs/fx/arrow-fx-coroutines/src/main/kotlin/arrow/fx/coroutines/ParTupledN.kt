package arrow.fx.coroutines

import arrow.core.Tuple4
import arrow.core.Tuple5
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Runs [fa], [fb] in parallel on [Dispatchers.Default] and combines their results into [Pair].
 *
 * ```kotlin:ank:playground
 * import arrow.fx.coroutines.*
 *
 * suspend fun main(): Unit {
 *   //sampleStart
 *   val (a, b) = parTupledN(
 *     { "First one is on ${Thread.currentThread().name}" },
 *     { "Second one is on ${Thread.currentThread().name}" }
 *   )
 *   //sampleEnd
 *  println("$a\n$b")
 * }
 * ```
 *
 * @param fa value to parallel map
 * @param fb value to parallel map
 *
 * @see parTupledN for a function that can run on any [CoroutineContext]
 */
suspend inline fun <A, B> parTupledN(
  crossinline fa: suspend () -> A,
  crossinline fb: suspend () -> B
): Pair<A, B> =
  parTupledN(Dispatchers.Default, fa, fb)

/**
 * Runs [fa], [fb] in parallel on [ctx] and combines their results into a [Pair]
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
 *   val (a, b) = parTupledN(
 *     Dispatchers.IO,
 *     { "First one is on ${Thread.currentThread().name}" },
 *     { "Second one is on ${Thread.currentThread().name}" }
 *   )
 *   //sampleEnd
 *  println("$a\n$b")
 * }
 * ```
 *
 * @param fa value to parallel map
 * @param fb value to parallel map
 *
 * @see parTupledN for a function that ensures operations run in parallel on the [Dispatchers.Default].
 */
suspend inline fun <A, B> parTupledN(
  ctx: CoroutineContext = EmptyCoroutineContext,
  crossinline fa: suspend () -> A,
  crossinline fb: suspend () -> B
): Pair<A, B> =
  parMapN(ctx, fa, fb, ::Pair)

/**
 * Runs [fa], [fb], [fc] in parallel on [Dispatchers.Default] and combines their results into [Triple].
 *
 * ```kotlin:ank:playground
 * import arrow.fx.coroutines.*
 *
 * suspend fun main(): Unit {
 *   //sampleStart
 *   val (a, b, c) = parTupledN(
 *     { "First one is on ${Thread.currentThread().name}" },
 *     { "Second one is on ${Thread.currentThread().name}" },
 *     { "Third one is on ${Thread.currentThread().name}" }
 *   )
 *   //sampleEnd
 *  println("$a\n$b\n$c")
 * }
 * ```
 *
 * @param fa value to parallel map
 * @param fb value to parallel map
 * @param fc value to parallel map
 *
 * @see parMapN for a function that can run on any [CoroutineContext].
 */
suspend inline fun <A, B, C> parTupledN(
  crossinline fa: suspend () -> A,
  crossinline fb: suspend () -> B,
  crossinline fc: suspend () -> C
): Triple<A, B, C> =
  parTupledN(Dispatchers.Default, fa, fb, fc)

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
 *   val (a, b, c) = parTupledN(
 *     Dispatchers.IO,
 *     { "First one is on ${Thread.currentThread().name}" },
 *     { "Second one is on ${Thread.currentThread().name}" },
 *     { "Third one is on ${Thread.currentThread().name}" }
 *   )
 *   //sampleEnd
 *  println("$a\n$b\n$c")
 * }
 * ```
 *
 * @param fa value to parallel map
 * @param fb value to parallel map
 * @param fc value to parallel map
 *
 * @see parTupledN for a function that ensures operations run in parallel on the [Dispatchers.Default].
 */
suspend inline fun <A, B, C> parTupledN(
  ctx: CoroutineContext = EmptyCoroutineContext,
  crossinline fa: suspend () -> A,
  crossinline fb: suspend () -> B,
  crossinline fc: suspend () -> C
): Triple<A, B, C> =
  parMapN(ctx, fa, fb, fc, ::Triple)

/**
 * Runs [fa], [fb], [fc], [fd] in parallel on [Dispatchers.Default] and combines their results into [Tuple4].
 *
 * ```kotlin:ank:playground
 * import arrow.fx.coroutines.*
 *
 * suspend fun main(): Unit {
 *   //sampleStart
 *   val (a, b, c, d) = parTupledN(
 *     { "First one is on ${Thread.currentThread().name}" },
 *     { "Second one is on ${Thread.currentThread().name}" },
 *     { "Third one is on ${Thread.currentThread().name}" },
 *     { "Fourth one is on ${Thread.currentThread().name}" }
 *   )
 *   //sampleEnd
 *  println("$a\n$b\n$c\n$d")
 * }
 * ```
 *
 * @param fa value to parallel map
 * @param fb value to parallel map
 * @param fc value to parallel map
 * @param fd value to parallel map
 *
 * @see parTupledN for a function that can run on any [CoroutineContext].
 */
suspend inline fun <A, B, C, D> parTupledN(
  crossinline fa: suspend () -> A,
  crossinline fb: suspend () -> B,
  crossinline fc: suspend () -> C,
  crossinline fd: suspend () -> D,
): Tuple4<A, B, C, D> =
  parTupledN(Dispatchers.Default, fa, fb, fc, fd)

/**
 * Runs [fa], [fb], [fc], [fd] in parallel on [ctx] and combines their results into [Tuple4].
 *
 * Coroutine context is inherited from a [CoroutineScope], additional context elements can be specified with [ctx] argument.
 * If the combined context does not have any dispatcher nor any other [ContinuationInterceptor], then [Dispatchers.Default] is used.
 * **WARNING** If the combined context has a single threaded [ContinuationInterceptor], this function will not run [fa], [fb], [fc] and [fd] in parallel.
 *
 * ```kotlin:ank:playground
 * import arrow.fx.coroutines.*
 * import kotlinx.coroutines.Dispatchers
 *
 * suspend fun main(): Unit {
 *   //sampleStart
 *   val (a, b, c, d) = parTupledN(
 *     Dispatchers.IO,
 *     { "First one is on ${Thread.currentThread().name}" },
 *     { "Second one is on ${Thread.currentThread().name}" },
 *     { "Third one is on ${Thread.currentThread().name}" },
 *     { "Forth one is on ${Thread.currentThread().name}" }
 *   )
 *   //sampleEnd
 *  println("$a\n$b\n$c\n$d")
 * }
 * ```
 *
 * @param fa value to parallel map
 * @param fb value to parallel map
 * @param fc value to parallel map
 * @param fd value to parallel map
 *
 * @see parTupledN for a function that ensures operations run in parallel on the [Dispatchers.Default].
 */
suspend inline fun <A, B, C, D> parTupledN(
  ctx: CoroutineContext = EmptyCoroutineContext,
  crossinline fa: suspend () -> A,
  crossinline fb: suspend () -> B,
  crossinline fc: suspend () -> C,
  crossinline fd: suspend () -> D,
): Tuple4<A, B, C, D> =
  parMapN(ctx, fa, fb, fc, fd, ::Tuple4)

/**
 * Runs [fa], [fb], [fc], [fd], [fe] in parallel on [Dispatchers.Default] and combines their results into [Tuple5].
 *
 * ```kotlin:ank:playground
 * import arrow.fx.coroutines.*
 *
 * suspend fun main(): Unit {
 *   //sampleStart
 *   val (a, b, c, d, e) = parTupledN(
 *     { "First one is on ${Thread.currentThread().name}" },
 *     { "Second one is on ${Thread.currentThread().name}" },
 *     { "Third one is on ${Thread.currentThread().name}" },
 *     { "Fourth one is on ${Thread.currentThread().name}" },
 *     { "Fifth one is on ${Thread.currentThread().name}" }
 *   )
 *   //sampleEnd
 *  println("$a\n$b\n$c\n$d\n$e")
 * }
 * ```
 *
 * @param fa value to parallel map
 * @param fb value to parallel map
 * @param fc value to parallel map
 * @param fd value to parallel map
 * @param fe value to parallel map
 *
 * @see parTupledN for a function that can run on any [CoroutineContext].
 */
suspend inline fun <A, B, C, D, E> parTupledN(
  crossinline fa: suspend () -> A,
  crossinline fb: suspend () -> B,
  crossinline fc: suspend () -> C,
  crossinline fd: suspend () -> D,
  crossinline fe: suspend () -> E
): Tuple5<A, B, C, D, E> =
  parTupledN(Dispatchers.Default, fa, fb, fc, fd, fe)

/**
 * Runs [fa], [fb], [fc], [fd], [fe] in parallel on [ctx] and combines their results into [Tuple5].
 *
 * Coroutine context is inherited from a [CoroutineScope], additional context elements can be specified with [ctx] argument.
 * If the combined context does not have any dispatcher nor any other [ContinuationInterceptor], then [Dispatchers.Default] is used.
 * **WARNING** If the combined context has a single threaded [ContinuationInterceptor], this function will not run [fa], [fb], [fc], [fd] and [fe] in parallel.
 *
 * ```kotlin:ank:playground
 * import arrow.fx.coroutines.*
 * import kotlinx.coroutines.Dispatchers
 *
 * suspend fun main(): Unit {
 *   //sampleStart
 *   val (a, b, c, d, e) = parTupledN(
 *     Dispatchers.IO,
 *     { "First one is on ${Thread.currentThread().name}" },
 *     { "Second one is on ${Thread.currentThread().name}" },
 *     { "Third one is on ${Thread.currentThread().name}" },
 *     { "Forth one is on ${Thread.currentThread().name}" },
 *     { "Fifth one is on ${Thread.currentThread().name}" }
 *   )
 *   //sampleEnd
 *  println("$a\n$b\n$c\n$d\n$e")
 * }
 * ```
 *
 * @param fa value to parallel map
 * @param fb value to parallel map
 * @param fc value to parallel map
 * @param fd value to parallel map
 * @param fe value to parallel map
 *
 * @see parTupledN for a function that ensures operations run in parallel on the [Dispatchers.Default].
 */
suspend inline fun <A, B, C, D, E> parTupledN(
  ctx: CoroutineContext = EmptyCoroutineContext,
  crossinline fa: suspend () -> A,
  crossinline fb: suspend () -> B,
  crossinline fc: suspend () -> C,
  crossinline fd: suspend () -> D,
  crossinline fe: suspend () -> E,
): Tuple5<A, B, C, D, E> =
  parMapN(ctx, fa, fb, fc, fd, fe, ::Tuple5)
