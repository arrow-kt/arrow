@file:Suppress("UNCHECKED_CAST")

package arrow.fx.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.awaitAll

/**
 * Runs [fa], [fb] in parallel on [Dispatchers.Default] and combines their results using the provided function.
 *
 * ```kotlin
 * import arrow.fx.coroutines.*
 *
 * suspend fun main(): Unit {
 *   //sampleStart
 *   val result = parZip(
 *     { "First one is on ${Thread.currentThread().name}" },
 *     { "Second one is on ${Thread.currentThread().name}" }
 *   ) { a, b ->
 *       "$a\n$b"
 *     }
 *   //sampleEnd
 *  println(result)
 * }
 * ```
 * <!--- KNIT example-parzip-01.kt -->
 *
 * @param fa value to parallel zip
 * @param fb value to parallel zip
 * @param f function to zip/combine value [A] and [B]
 *
 * @see parZip for a function that can run on any [CoroutineContext]
 */
public suspend inline fun <A, B, C> parZip(
  crossinline fa: suspend CoroutineScope.() -> A,
  crossinline fb: suspend CoroutineScope.() -> B,
  crossinline f: suspend CoroutineScope.(A, B) -> C
): C = parZip(Dispatchers.Default, fa, fb, f)

/**
 * Runs [fa], [fb] in parallel on [ctx] and combines their results using the provided function.
 *
 * Coroutine context is inherited from a [CoroutineScope], additional context elements can be specified with [ctx] argument.
 * If the combined context does not have any dispatcher nor any other [ContinuationInterceptor], then [Dispatchers.Default] is used.
 * **WARNING** If the combined context has a single threaded [ContinuationInterceptor], this function will not run [fa], [fb] in parallel.
 *
 * ```kotlin
 * import arrow.fx.coroutines.*
 * import kotlinx.coroutines.Dispatchers
 *
 * suspend fun main(): Unit {
 *   //sampleStart
 *   val result = parZip(
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
 * <!--- KNIT example-parzip-02.kt -->
 *
 * @param fa value to parallel zip
 * @param fb value to parallel zip
 * @param f function to zip/combine value [A] and [B]
 *
 * @see parZip for a function that ensures operations run in parallel on the [Dispatchers.Default].
 */
public suspend inline fun <A, B, C> parZip(
  ctx: CoroutineContext = EmptyCoroutineContext,
  crossinline fa: suspend CoroutineScope.() -> A,
  crossinline fb: suspend CoroutineScope.() -> B,
  crossinline f: suspend CoroutineScope.(A, B) -> C
): C = coroutineScope {
  val faa = async(ctx) { fa() }
  val fbb = async(ctx) { fb() }
  val (a, b) = awaitAll(faa, fbb)
  f(a as A, b as B)
}

/**
 * Runs [fa], [fb], [fc] in parallel on [Dispatchers.Default] and combines their results using the provided function.
 *
 * ```kotlin
 * import arrow.fx.coroutines.*
 *
 * suspend fun main(): Unit {
 *   //sampleStart
 *   val result = parZip(
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
 * <!--- KNIT example-parzip-03.kt -->
 *
 * @param fa value to parallel zip
 * @param fb value to parallel zip
 * @param fc value to parallel zip
 * @param f function to zip/combine value [A], [B] and [C]
 *
 * @see parZip for a function that can run on any [CoroutineContext].
 */
public suspend inline fun <A, B, C, D> parZip(
  crossinline fa: suspend CoroutineScope.() -> A,
  crossinline fb: suspend CoroutineScope.() -> B,
  crossinline fc: suspend CoroutineScope.() -> C,
  crossinline f: suspend CoroutineScope.(A, B, C) -> D
): D = parZip(Dispatchers.Default, fa, fb, fc, f)

/**
 * Runs [fa], [fb], [fc] in parallel on [ctx] and combines their results using the provided function.
 *
 * Coroutine context is inherited from a [CoroutineScope], additional context elements can be specified with [ctx] argument.
 * If the combined context does not have any dispatcher nor any other [ContinuationInterceptor], then [Dispatchers.Default] is used.
 * **WARNING** If the combined context has a single threaded [ContinuationInterceptor], this function will not run [fa], [fb] & [fc] in parallel.
 *
 * ```kotlin
 * import arrow.fx.coroutines.*
 * import kotlinx.coroutines.Dispatchers
 *
 * suspend fun main(): Unit {
 *   //sampleStart
 *   val result = parZip(
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
 * <!--- KNIT example-parzip-04.kt -->
 *
 * @param fa value to parallel zip
 * @param fb value to parallel zip
 * @param fc value to parallel zip
 * @param f function to zip/combine value [A], [B] and [C].
 *
 * @see parZip for a function that ensures operations run in parallel on the [Dispatchers.Default].
 */
public suspend inline fun <A, B, C, D> parZip(
  ctx: CoroutineContext = EmptyCoroutineContext,
  crossinline fa: suspend CoroutineScope.() -> A,
  crossinline fb: suspend CoroutineScope.() -> B,
  crossinline fc: suspend CoroutineScope.() -> C,
  crossinline f: suspend CoroutineScope.(A, B, C) -> D
): D = coroutineScope {
  val faa = async(ctx) { fa() }
  val fbb = async(ctx) { fb() }
  val fcc = async(ctx) { fc() }
  val (a, b, c) = awaitAll(faa, fbb, fcc)
  f(a as A, b as B, c as C)
}

/**
 * Runs [fa], [fb], [fc], [fd] in parallel on [Dispatchers.Default] and combines their results using the provided function.
 *
 * ```kotlin
 * import arrow.fx.coroutines.*
 *
 * suspend fun main(): Unit {
 *   //sampleStart
 *   val result = parZip(
 *     { "First one is on ${Thread.currentThread().name}" },
 *     { "Second one is on ${Thread.currentThread().name}" },
 *     { "Third one is on ${Thread.currentThread().name}" },
 *     { "Fourth one is on ${Thread.currentThread().name}" }
 *   ) { a, b, c, d ->
 *       "$a\n$b\n$c\n$d"
 *     }
 *   //sampleEnd
 *  println(result)
 * }
 * ```
 * <!--- KNIT example-parzip-05.kt -->
 *
 * @param fa value to parallel zip
 * @param fb value to parallel zip
 * @param fc value to parallel zip
 * @param fd value to parallel zip
 * @param f function to zip/combine value [A], [B], [C] and [D]
 *
 * @see parZip for a function that can run on any [CoroutineContext].
 */
public suspend inline fun <A, B, C, D, E> parZip(
  crossinline fa: suspend CoroutineScope.() -> A,
  crossinline fb: suspend CoroutineScope.() -> B,
  crossinline fc: suspend CoroutineScope.() -> C,
  crossinline fd: suspend CoroutineScope.() -> D,
  crossinline f: suspend CoroutineScope.(A, B, C, D) -> E
): E = parZip(Dispatchers.Default, fa, fb, fc, fd, f)

/**
 * Runs [fa], [fb], [fc], [fd] in parallel on [ctx] and combines their results using the provided function.
 *
 * Coroutine context is inherited from a [CoroutineScope], additional context elements can be specified with [ctx] argument.
 * If the combined context does not have any dispatcher nor any other [ContinuationInterceptor], then [Dispatchers.Default] is used.
 * **WARNING** If the combined context has a single threaded [ContinuationInterceptor], this function will not run [fa], [fb], [fc] & [fd]
 * in parallel.
 *
 * ```kotlin
 * import arrow.fx.coroutines.*
 * import kotlinx.coroutines.Dispatchers
 *
 * suspend fun main(): Unit {
 *   //sampleStart
 *   val result = parZip(
 *     Dispatchers.IO,
 *     { "First one is on ${Thread.currentThread().name}" },
 *     { "Second one is on ${Thread.currentThread().name}" },
 *     { "Third one is on ${Thread.currentThread().name}" },
 *     { "Fourth one is on ${Thread.currentThread().name}" }
 *   ) { a, b, c, d ->
 *       "$a\n$b\n$c\n$d"
 *     }
 *   //sampleEnd
 *  println(result)
 * }
 * ```
 * <!--- KNIT example-parzip-06.kt -->
 *
 * @param fa value to parallel zip
 * @param fb value to parallel zip
 * @param fc value to parallel zip
 * @param fd value to parallel zip
 * @param f function to zip/combine value [A], [B], [C] and [D].
 *
 * @see parZip for a function that ensures operations run in parallel on the [Dispatchers.Default].
 */
public suspend inline fun <A, B, C, D, E> parZip(
  ctx: CoroutineContext = EmptyCoroutineContext,
  crossinline fa: suspend CoroutineScope.() -> A,
  crossinline fb: suspend CoroutineScope.() -> B,
  crossinline fc: suspend CoroutineScope.() -> C,
  crossinline fd: suspend CoroutineScope.() -> D,
  crossinline f: suspend CoroutineScope.(A, B, C, D) -> E
): E = coroutineScope {
  val faa = async(ctx) { fa() }
  val fbb = async(ctx) { fb() }
  val fcc = async(ctx) { fc() }
  val fdd = async(ctx) { fd() }
  val (a, b, c, d) = awaitAll(faa, fbb, fcc, fdd)
  f(a as A, b as B, c as C, d as D)
}

/**
 * Runs [fa], [fb], [fc], [fd], [fe] in parallel on [Dispatchers.Default] and combines
 * their results using the provided function.
 *
 * ```kotlin
 * import arrow.fx.coroutines.*
 *
 * suspend fun main(): Unit {
 *   //sampleStart
 *   val result = parZip(
 *     { "First one is on ${Thread.currentThread().name}" },
 *     { "Second one is on ${Thread.currentThread().name}" },
 *     { "Third one is on ${Thread.currentThread().name}" },
 *     { "Fourth one is on ${Thread.currentThread().name}" },
 *     { "Fifth one is on ${Thread.currentThread().name}" }
 *   ) { a, b, c, d, e ->
 *       "$a\n$b\n$c\n$d\n$e"
 *     }
 *   //sampleEnd
 *  println(result)
 * }
 * ```
 * <!--- KNIT example-parzip-07.kt -->
 *
 * @param fa value to parallel zip
 * @param fb value to parallel zip
 * @param fc value to parallel zip
 * @param fd value to parallel zip
 * @param fe value to parallel zip
 * @param f function to zip/combine value [A], [B], [C], [D] and [E]
 *
 * @see parZip for a function that can run on any [CoroutineContext].
 */
public suspend inline fun <A, B, C, D, E, F> parZip(
  crossinline fa: suspend CoroutineScope.() -> A,
  crossinline fb: suspend CoroutineScope.() -> B,
  crossinline fc: suspend CoroutineScope.() -> C,
  crossinline fd: suspend CoroutineScope.() -> D,
  crossinline fe: suspend CoroutineScope.() -> E,
  crossinline f: suspend CoroutineScope.(A, B, C, D, E) -> F
): F = parZip(Dispatchers.Default, fa, fb, fc, fd, fe, f)

/**
 * Runs [fa], [fb], [fc], [fd], [fe] in parallel on [ctx] and combines their results using the provided function.
 *
 * Coroutine context is inherited from a [CoroutineScope], additional context elements can be specified with [ctx] argument.
 * If the combined context does not have any dispatcher nor any other [ContinuationInterceptor], then [Dispatchers.Default] is used.
 * **WARNING** If the combined context has a single threaded [ContinuationInterceptor], this function will not run [fa], [fb], [fc], [fd] & [fe]
 * in parallel.
 *
 * ```kotlin
 * import arrow.fx.coroutines.*
 * import kotlinx.coroutines.Dispatchers
 *
 * suspend fun main(): Unit {
 *   //sampleStart
 *   val result = parZip(
 *     Dispatchers.IO,
 *     { "First one is on ${Thread.currentThread().name}" },
 *     { "Second one is on ${Thread.currentThread().name}" },
 *     { "Third one is on ${Thread.currentThread().name}" },
 *     { "Fourth one is on ${Thread.currentThread().name}" },
 *     { "Fifth one is on ${Thread.currentThread().name}" }
 *   ) { a, b, c, d, e ->
 *       "$a\n$b\n$c\n$d\n$e"
 *     }
 *   //sampleEnd
 *  println(result)
 * }
 * ```
 * <!--- KNIT example-parzip-08.kt -->
 *
 * @param fa value to parallel zip
 * @param fb value to parallel zip
 * @param fc value to parallel zip
 * @param fd value to parallel zip
 * @param fe value to parallel zip
 * @param f function to zip/combine value [A], [B], [C], [D], and [E].
 *
 * @see parZip for a function that ensures operations run in parallel on the [Dispatchers.Default].
 */
public suspend inline fun <A, B, C, D, E, F> parZip(
  ctx: CoroutineContext = EmptyCoroutineContext,
  crossinline fa: suspend CoroutineScope.() -> A,
  crossinline fb: suspend CoroutineScope.() -> B,
  crossinline fc: suspend CoroutineScope.() -> C,
  crossinline fd: suspend CoroutineScope.() -> D,
  crossinline fe: suspend CoroutineScope.() -> E,
  crossinline f: suspend CoroutineScope.(A, B, C, D, E) -> F
): F = coroutineScope {
  val faa = async(ctx) { fa() }
  val fbb = async(ctx) { fb() }
  val fcc = async(ctx) { fc() }
  val fdd = async(ctx) { fd() }
  val fee = async(ctx) { fe() }
  val (a, b, c, d, e) = awaitAll(faa, fbb, fcc, fdd, fee)
  f(a as A, b as B, c as C, d as D, e as E)
}

/**
 * Runs [fa], [fb], [fc], [fd], [fe], [ff] in parallel on [Dispatchers.Default] and combines
 * their results using the provided function.
 *
 * ```kotlin
 * import arrow.fx.coroutines.*
 *
 * suspend fun main(): Unit {
 *   //sampleStart
 *   val result = parZip(
 *     { "First one is on ${Thread.currentThread().name}" },
 *     { "Second one is on ${Thread.currentThread().name}" },
 *     { "Third one is on ${Thread.currentThread().name}" },
 *     { "Fourth one is on ${Thread.currentThread().name}" },
 *     { "Fifth one is on ${Thread.currentThread().name}" },
 *     { "Sixth one is on ${Thread.currentThread().name}" }
 *   ) { a, b, c, d, e, f ->
 *       "$a\n$b\n$c\n$d\n$e\n$f"
 *     }
 *   //sampleEnd
 *  println(result)
 * }
 * ```
 * <!--- KNIT example-parzip-09.kt -->
 *
 * @param fa value to parallel zip
 * @param fb value to parallel zip
 * @param fc value to parallel zip
 * @param fd value to parallel zip
 * @param fe value to parallel zip
 * @param ff value to parallel zip
 * @param f function to zip/combine value [A], [B], [C], [D], [E] and [F]
 *
 * @see parZip for a function that can run on any [CoroutineContext].
 */
public suspend inline fun <A, B, C, D, E, F, G> parZip(
  crossinline fa: suspend CoroutineScope.() -> A,
  crossinline fb: suspend CoroutineScope.() -> B,
  crossinline fc: suspend CoroutineScope.() -> C,
  crossinline fd: suspend CoroutineScope.() -> D,
  crossinline fe: suspend CoroutineScope.() -> E,
  crossinline ff: suspend CoroutineScope.() -> F,
  crossinline f: suspend CoroutineScope.(A, B, C, D, E, F) -> G
): G = parZip(Dispatchers.Default, fa, fb, fc, fd, fe, ff, f)

/**
 * Runs [fa], [fb], [fc], [fd], [fe], [ff] in parallel on [ctx] and combines their results using the provided function.
 *
 * Coroutine context is inherited from a [CoroutineScope], additional context elements can be specified with [ctx] argument.
 * If the combined context does not have any dispatcher nor any other [ContinuationInterceptor], then [Dispatchers.Default] is used.
 * **WARNING** If the combined context has a single threaded [ContinuationInterceptor], this function will not run [fa], [fb], [fc], [fd], [fe] & [ff]
 * in parallel.
 *
 * ```kotlin
 * import arrow.fx.coroutines.*
 * import kotlinx.coroutines.Dispatchers
 *
 * suspend fun main(): Unit {
 *   //sampleStart
 *   val result = parZip(
 *     Dispatchers.IO,
 *     { "First one is on ${Thread.currentThread().name}" },
 *     { "Second one is on ${Thread.currentThread().name}" },
 *     { "Third one is on ${Thread.currentThread().name}" },
 *     { "Fourth one is on ${Thread.currentThread().name}" },
 *     { "Fifth one is on ${Thread.currentThread().name}" },
 *     { "Sixth one is on ${Thread.currentThread().name}" }
 *   ) { a, b, c, d, e, g ->
 *       "$a\n$b\n$c\n$d\n$e\n$g"
 *     }
 *   //sampleEnd
 *  println(result)
 * }
 * ```
 * <!--- KNIT example-parzip-10.kt -->
 *
 * @param fa value to parallel zip
 * @param fb value to parallel zip
 * @param fc value to parallel zip
 * @param fd value to parallel zip
 * @param fe value to parallel zip
 * @param ff value to parallel zip
 * @param f function to zip/combine value [A], [B], [C], [D], [E] and [F]
 *
 * @see parZip for a function that ensures operations run in parallel on the [Dispatchers.Default].
 */
public suspend inline fun <A, B, C, D, E, F, G> parZip(
  ctx: CoroutineContext = EmptyCoroutineContext,
  crossinline fa: suspend CoroutineScope.() -> A,
  crossinline fb: suspend CoroutineScope.() -> B,
  crossinline fc: suspend CoroutineScope.() -> C,
  crossinline fd: suspend CoroutineScope.() -> D,
  crossinline fe: suspend CoroutineScope.() -> E,
  crossinline ff: suspend CoroutineScope.() -> F,
  crossinline f: suspend CoroutineScope.(A, B, C, D, E, F) -> G
): G = coroutineScope {
  val faa = async(ctx) { fa() }
  val fbb = async(ctx) { fb() }
  val fcc = async(ctx) { fc() }
  val fdd = async(ctx) { fd() }
  val fee = async(ctx) { fe() }
  val fgg = async(ctx) { ff() }
  val res = awaitAll(faa, fbb, fcc, fdd, fee, fgg)
  f(res[0] as A, res[1] as B, res[2] as C, res[3] as D, res[4] as E, res[5] as F)
}

/**
 * Runs [fa], [fb], [fc], [fd], [fe], [ff], [fg] in parallel on [Dispatchers.Default] and combines
 * their results using the provided function.
 *
 * ```kotlin
 * import arrow.fx.coroutines.*
 *
 * suspend fun main(): Unit {
 *   //sampleStart
 *   val result = parZip(
 *     { "First one is on ${Thread.currentThread().name}" },
 *     { "Second one is on ${Thread.currentThread().name}" },
 *     { "Third one is on ${Thread.currentThread().name}" },
 *     { "Fourth one is on ${Thread.currentThread().name}" },
 *     { "Fifth one is on ${Thread.currentThread().name}" },
 *     { "Sixth one is on ${Thread.currentThread().name}" },
 *     { "Seventh one is on ${Thread.currentThread().name}" }
 *   ) { a, b, c, d, e, g, h ->
 *       "$a\n$b\n$c\n$d\n$e\n$g\n$h"
 *     }
 *   //sampleEnd
 *  println(result)
 * }
 * ```
 * <!--- KNIT example-parzip-11.kt -->
 *
 * @param fa value to parallel zip
 * @param fb value to parallel zip
 * @param fc value to parallel zip
 * @param fd value to parallel zip
 * @param fe value to parallel zip
 * @param ff value to parallel zip
 * @param fg value to parallel zip
 * @param f function to zip/combine value [A], [B], [C], [D], [E], [F] and [G]
 *
 * @see parZip for a function that can run on any [CoroutineContext].
 */
public suspend inline fun <A, B, C, D, E, F, G, H> parZip(
  crossinline fa: suspend CoroutineScope.() -> A,
  crossinline fb: suspend CoroutineScope.() -> B,
  crossinline fc: suspend CoroutineScope.() -> C,
  crossinline fd: suspend CoroutineScope.() -> D,
  crossinline fe: suspend CoroutineScope.() -> E,
  crossinline ff: suspend CoroutineScope.() -> F,
  crossinline fg: suspend CoroutineScope.() -> G,
  crossinline f: suspend CoroutineScope.(A, B, C, D, E, F, G) -> H
): H = parZip(Dispatchers.Default, fa, fb, fc, fd, fe, ff, fg, f)

/**
 * Runs [fa], [fb], [fc], [fd], [fe], [ff], [fg] in parallel on [ctx] and combines their results using the provided function.
 *
 * Coroutine context is inherited from a [CoroutineScope], additional context elements can be specified with [ctx] argument.
 * If the combined context does not have any dispatcher nor any other [ContinuationInterceptor], then [Dispatchers.Default] is used.
 * **WARNING** If the combined context has a single threaded [ContinuationInterceptor], this function will not run [fa], [fb], [fc], [fd], [fe], [ff] & [fg]
 * in parallel.
 *
 * ```kotlin
 * import arrow.fx.coroutines.*
 * import kotlinx.coroutines.Dispatchers
 *
 * suspend fun main(): Unit {
 *   //sampleStart
 *   val result = parZip(
 *     Dispatchers.IO,
 *     { "First one is on ${Thread.currentThread().name}" },
 *     { "Second one is on ${Thread.currentThread().name}" },
 *     { "Third one is on ${Thread.currentThread().name}" },
 *     { "Fourth one is on ${Thread.currentThread().name}" },
 *     { "Fifth one is on ${Thread.currentThread().name}" },
 *     { "Sixth one is on ${Thread.currentThread().name}" },
 *     { "Seventh one is on ${Thread.currentThread().name}" }
 *   ) { a, b, c, d, e, f, g ->
 *       "$a\n$b\n$c\n$d\n$e\n$f\n$g"
 *     }
 *   //sampleEnd
 *  println(result)
 * }
 * ```
 * <!--- KNIT example-parzip-12.kt -->
 *
 * @param fa value to parallel zip
 * @param fb value to parallel zip
 * @param fc value to parallel zip
 * @param fd value to parallel zip
 * @param fe value to parallel zip
 * @param ff value to parallel zip
 * @param fg value to parallel zip
 * @param f function to zip/combine value [A], [B], [C], [D], [E], [F] and [G]
 *
 * @see parZip for a function that ensures operations run in parallel on the [Dispatchers.Default].
 */
public suspend inline fun <A, B, C, D, E, F, G, H> parZip(
  ctx: CoroutineContext = EmptyCoroutineContext,
  crossinline fa: suspend CoroutineScope.() -> A,
  crossinline fb: suspend CoroutineScope.() -> B,
  crossinline fc: suspend CoroutineScope.() -> C,
  crossinline fd: suspend CoroutineScope.() -> D,
  crossinline fe: suspend CoroutineScope.() -> E,
  crossinline ff: suspend CoroutineScope.() -> F,
  crossinline fg: suspend CoroutineScope.() -> G,
  crossinline f: suspend CoroutineScope.(A, B, C, D, E, F, G) -> H
): H = coroutineScope {
  val faa = async(ctx) { fa() }
  val fbb = async(ctx) { fb() }
  val fcc = async(ctx) { fc() }
  val fdd = async(ctx) { fd() }
  val fee = async(ctx) { fe() }
  val fDef = async(ctx) { ff() }
  val fgg = async(ctx) { fg() }
  val res = awaitAll(faa, fbb, fcc, fdd, fee, fDef, fgg)
  f(res[0] as A, res[1] as B, res[2] as C, res[3] as D, res[4] as E, res[5] as F, res[6] as G)
}

/**
 * Runs [fa], [fb], [fc], [fd], [fe], [ff], [fg], [fh] in parallel on [Dispatchers.Default] and combines
 * their results using the provided function.
 *
 * ```kotlin
 * import arrow.fx.coroutines.*
 *
 * suspend fun main(): Unit {
 *   //sampleStart
 *   val result = parZip(
 *     { "First one is on ${Thread.currentThread().name}" },
 *     { "Second one is on ${Thread.currentThread().name}" },
 *     { "Third one is on ${Thread.currentThread().name}" },
 *     { "Fourth one is on ${Thread.currentThread().name}" },
 *     { "Fifth one is on ${Thread.currentThread().name}" },
 *     { "Sixth one is on ${Thread.currentThread().name}" },
 *     { "Seventh one is on ${Thread.currentThread().name}" },
 *     { "Eighth one is on ${Thread.currentThread().name}" }
 *   ) { a, b, c, d, e, f, g, h ->
 *       "$a\n$b\n$c\n$d\n$e\n$f\n$g\n$h"
 *     }
 *   //sampleEnd
 *  println(result)
 * }
 * ```
 * <!--- KNIT example-parzip-13.kt -->
 *
 * @param fa value to parallel zip
 * @param fb value to parallel zip
 * @param fc value to parallel zip
 * @param fd value to parallel zip
 * @param fe value to parallel zip
 * @param ff value to parallel zip
 * @param fg value to parallel zip
 * @param fh value to parallel zip
 * @param f function to zip/combine value [A], [B], [C], [D], [E], [F], [G] and [H]
 *
 * @see parZip for a function that can run on any [CoroutineContext].
 */
public suspend inline fun <A, B, C, D, E, F, G, H, I> parZip(
  crossinline fa: suspend CoroutineScope.() -> A,
  crossinline fb: suspend CoroutineScope.() -> B,
  crossinline fc: suspend CoroutineScope.() -> C,
  crossinline fd: suspend CoroutineScope.() -> D,
  crossinline fe: suspend CoroutineScope.() -> E,
  crossinline ff: suspend CoroutineScope.() -> F,
  crossinline fg: suspend CoroutineScope.() -> G,
  crossinline fh: suspend CoroutineScope.() -> H,
  crossinline f: suspend CoroutineScope.(A, B, C, D, E, F, G, H) -> I
): I = parZip(Dispatchers.Default, fa, fb, fc, fd, fe, ff, fg, fh, f)

/**
 * Runs [fa], [fb], [fc], [fd], [fe], [ff], [fg], [fh] in parallel on [ctx] and combines their results using the provided function.
 *
 * Coroutine context is inherited from a [CoroutineScope], additional context elements can be specified with [ctx] argument.
 * If the combined context does not have any dispatcher nor any other [ContinuationInterceptor], then [Dispatchers.Default] is used.
 * **WARNING** If the combined context has a single threaded [ContinuationInterceptor], this function will not run [fa], [fb], [fc], [fd], [fe], [ff] & [fg]
 * in parallel.
 *
 * ```kotlin
 * import arrow.fx.coroutines.*
 * import kotlinx.coroutines.Dispatchers
 *
 * suspend fun main(): Unit {
 *   //sampleStart
 *   val result = parZip(
 *     Dispatchers.IO,
 *     { "First one is on ${Thread.currentThread().name}" },
 *     { "Second one is on ${Thread.currentThread().name}" },
 *     { "Third one is on ${Thread.currentThread().name}" },
 *     { "Fourth one is on ${Thread.currentThread().name}" },
 *     { "Fifth one is on ${Thread.currentThread().name}" },
 *     { "Sixth one is on ${Thread.currentThread().name}" },
 *     { "Seventh one is on ${Thread.currentThread().name}" },
 *     fh = { "Eighth one is on ${Thread.currentThread().name}" }
 *   ) { a, b, c, d, e, f, g, h ->
 *       "$a\n$b\n$c\n$d\n$e\n$f\n$g\n$h"
 *     }
 *   //sampleEnd
 *  println(result)
 * }
 * ```
 * <!--- KNIT example-parzip-14.kt -->
 *
 * @param fa value to parallel zip
 * @param fb value to parallel zip
 * @param fc value to parallel zip
 * @param fd value to parallel zip
 * @param fe value to parallel zip
 * @param ff value to parallel zip
 * @param fg value to parallel zip
 * @param fh value to parallel zip
 * @param f function to zip/combine value [A], [B], [C], [D], [E], [F], [G] and [H]
 *
 * @see parZip for a function that ensures operations run in parallel on the [Dispatchers.Default].
 */
public suspend inline fun <A, B, C, D, E, F, G, H, I> parZip(
  ctx: CoroutineContext = EmptyCoroutineContext,
  crossinline fa: suspend CoroutineScope.() -> A,
  crossinline fb: suspend CoroutineScope.() -> B,
  crossinline fc: suspend CoroutineScope.() -> C,
  crossinline fd: suspend CoroutineScope.() -> D,
  crossinline fe: suspend CoroutineScope.() -> E,
  crossinline ff: suspend CoroutineScope.() -> F,
  crossinline fg: suspend CoroutineScope.() -> G,
  crossinline fh: suspend CoroutineScope.() -> H,
  crossinline f: suspend CoroutineScope.(A, B, C, D, E, F, G, H) -> I
): I = coroutineScope {
  val faa = async(ctx) { fa() }
  val fbb = async(ctx) { fb() }
  val fcc = async(ctx) { fc() }
  val fdd = async(ctx) { fd() }
  val fee = async(ctx) { fe() }
  val fDef = async(ctx) { ff() }
  val fgg = async(ctx) { fg() }
  val fhh = async(ctx) { fh() }
  val res = awaitAll(faa, fbb, fcc, fdd, fee, fDef, fgg, fhh)
  f(res[0] as A, res[1] as B, res[2] as C, res[3] as D, res[4] as E, res[5] as F, res[6] as G, res[7] as H)
}

/**
 * Runs [fa], [fb], [fc], [fd], [fe], [ff], [fg], [fh], [fi] in parallel on [Dispatchers.Default] and combines
 * their results using the provided function.
 *
 * ```kotlin
 * import arrow.fx.coroutines.*
 *
 * suspend fun main(): Unit {
 *   //sampleStart
 *   val result = parZip(
 *     { "First one is on ${Thread.currentThread().name}" },
 *     { "Second one is on ${Thread.currentThread().name}" },
 *     { "Third one is on ${Thread.currentThread().name}" },
 *     { "Fourth one is on ${Thread.currentThread().name}" },
 *     { "Fifth one is on ${Thread.currentThread().name}" },
 *     { "Sixth one is on ${Thread.currentThread().name}" },
 *     { "Seventh one is on ${Thread.currentThread().name}" },
 *     { "Eighth one is on ${Thread.currentThread().name}" },
 *     fi = { "Ninth one is on ${Thread.currentThread().name}" }
 *   ) { a, b, c, d, e, f, g, h, i->
 *       "$a\n$b\n$c\n$d\n$e\n$f\n$g\n$h\n$i"
 *     }
 *   //sampleEnd
 *  println(result)
 * }
 * ```
 * <!--- KNIT example-parzip-15.kt -->
 *
 * @param fa value to parallel zip
 * @param fb value to parallel zip
 * @param fc value to parallel zip
 * @param fd value to parallel zip
 * @param fe value to parallel zip
 * @param ff value to parallel zip
 * @param fg value to parallel zip
 * @param fh value to parallel zip
 * @param fi value to parallel zip
 * @param f function to zip/combine value [A], [B], [C], [D], [E], [F], [G], [H], [I]
 *
 * @see parZip for a function that can run on any [CoroutineContext].
 */
public suspend inline fun <A, B, C, D, E, F, G, H, I, J> parZip(
  crossinline fa: suspend CoroutineScope.() -> A,
  crossinline fb: suspend CoroutineScope.() -> B,
  crossinline fc: suspend CoroutineScope.() -> C,
  crossinline fd: suspend CoroutineScope.() -> D,
  crossinline fe: suspend CoroutineScope.() -> E,
  crossinline ff: suspend CoroutineScope.() -> F,
  crossinline fg: suspend CoroutineScope.() -> G,
  crossinline fh: suspend CoroutineScope.() -> H,
  crossinline fi: suspend CoroutineScope.() -> I,
  crossinline f: suspend CoroutineScope.(A, B, C, D, E, F, G, H, I) -> J
): J = parZip(Dispatchers.Default, fa, fb, fc, fd, fe, ff, fg, fh, fi, f)

/**
 * Runs [fa], [fb], [fc], [fd], [fe], [ff], [fg], [fh], [fi] in parallel on [ctx] and combines their results using the provided function.
 *
 * Coroutine context is inherited from a [CoroutineScope], additional context elements can be specified with [ctx] argument.
 * If the combined context does not have any dispatcher nor any other [ContinuationInterceptor], then [Dispatchers.Default] is used.
 * **WARNING** If the combined context has a single threaded [ContinuationInterceptor], this function will not run [fa], [fb], [fc], [fd], [fe], [ff], [fg], [fh] & [fi]
 * in parallel.
 *
 * ```kotlin
 * import arrow.fx.coroutines.*
 * import kotlinx.coroutines.Dispatchers
 *
 * suspend fun main(): Unit {
 *   //sampleStart
 *   val result = parZip(
 *     Dispatchers.IO,
 *     { "First one is on ${Thread.currentThread().name}" },
 *     { "Second one is on ${Thread.currentThread().name}" },
 *     { "Third one is on ${Thread.currentThread().name}" },
 *     { "Fourth one is on ${Thread.currentThread().name}" },
 *     { "Fifth one is on ${Thread.currentThread().name}" },
 *     { "Sixth one is on ${Thread.currentThread().name}" },
 *     { "Seventh one is on ${Thread.currentThread().name}" },
 *     { "Eighth one is on ${Thread.currentThread().name}" },
 *     fi = { "Ninth one is on ${Thread.currentThread().name}" }
 *   ) { a, b, c, d, e, f, g, h, i->
 *       "$a\n$b\n$c\n$d\n$e\n$f\n$g\n$h\n$i"
 *     }
 *   //sampleEnd
 *  println(result)
 * }
 * ```
 * <!--- KNIT example-parzip-16.kt -->
 *
 * @param fa value to parallel zip
 * @param fb value to parallel zip
 * @param fc value to parallel zip
 * @param fd value to parallel zip
 * @param fe value to parallel zip
 * @param ff value to parallel zip
 * @param fg value to parallel zip
 * @param fh value to parallel zip
 * @param fi value to parallel zip
 * @param f function to zip/combine value [A], [B], [C], [D], [E], [F], [G], [H] and [I]
 *
 * @see parZip for a function that ensures operations run in parallel on the [Dispatchers.Default].
 */
public suspend inline fun <A, B, C, D, E, F, G, H, I, J> parZip(
  ctx: CoroutineContext = EmptyCoroutineContext,
  crossinline fa: suspend CoroutineScope.() -> A,
  crossinline fb: suspend CoroutineScope.() -> B,
  crossinline fc: suspend CoroutineScope.() -> C,
  crossinline fd: suspend CoroutineScope.() -> D,
  crossinline fe: suspend CoroutineScope.() -> E,
  crossinline ff: suspend CoroutineScope.() -> F,
  crossinline fg: suspend CoroutineScope.() -> G,
  crossinline fh: suspend CoroutineScope.() -> H,
  crossinline fi: suspend CoroutineScope.() -> I,
  crossinline f: suspend CoroutineScope.(A, B, C, D, E, F, G, H, I) -> J
): J = coroutineScope {
  val faa = async(ctx) { fa() }
  val fbb = async(ctx) { fb() }
  val fcc = async(ctx) { fc() }
  val fdd = async(ctx) { fd() }
  val fee = async(ctx) { fe() }
  val fDef = async(ctx) { ff() }
  val fgg = async(ctx) { fg() }
  val fhh = async(ctx) { fh() }
  val fii = async(ctx) { fi() }
  val res = awaitAll(faa, fbb, fcc, fdd, fee, fDef, fgg, fhh, fii)
  f(res[0] as A, res[1] as B, res[2] as C, res[3] as D, res[4] as E, res[5] as F, res[6] as G, res[7] as H, res[8] as I)
}

public suspend fun <A> parZip(
  ctx: CoroutineContext = EmptyCoroutineContext,
  block: suspend ParZipScope.() -> A
): A = coroutineScope {
  block(ParZipScope(this@coroutineScope, ctx))
}

public class ParZipScope(
  private val scope: CoroutineScope,
  private val ctx: CoroutineContext
): CoroutineScope by scope {
  private val tasks: MutableList<Deferred<*>> = mutableListOf()

  internal suspend fun runTasks() = tasks.awaitAll()

  public fun <A> concurrently(block: suspend CoroutineScope.() -> A): Value<A> {
    val task = async(context = ctx, block = block)
    tasks.add(task)
    return Value(task)
  }

  public inner class Value<A>(private val task: Deferred<A>) {
    // public suspend operator fun getValue(value: Nothing?, property: KProperty<*>): A {
    public suspend fun value(): A {
      runTasks()
      return task.await()
    }
  }
}
