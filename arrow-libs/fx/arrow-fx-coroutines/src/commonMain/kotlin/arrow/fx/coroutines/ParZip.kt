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
 *
 * @param fa value to parallel map
 * @param fb value to parallel map
 * @param f function to map/combine value [A] and [B]
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
 * ```kotlin:ank:playground
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
 *
 * @param fa value to parallel map
 * @param fb value to parallel map
 * @param f function to map/combine value [A] and [B]
 *
 * @see parZip for a function that ensures operations run in parallel on the [Dispatchers.Default].
 */
public suspend inline fun <A, B, C> parZip(
  ctx: CoroutineContext = EmptyCoroutineContext,
  crossinline fa: suspend CoroutineScope.() -> A,
  crossinline fb: suspend CoroutineScope.() -> B,
  crossinline f: suspend CoroutineScope.(A, B) -> C
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
 *
 * @param fa value to parallel map
 * @param fb value to parallel map
 * @param fc value to parallel map
 * @param f function to map/combine value [A], [B] and [C]
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
 * ```kotlin:ank:playground
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
 *
 * @param fa value to parallel map
 * @param fb value to parallel map
 * @param fc value to parallel map
 * @param f function to map/combine value [A], [B] and [C].
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
  val a = async(ctx) { fa() }
  val b = async(ctx) { fb() }
  val c = async(ctx) { fc() }
  f(a.await(), b.await(), c.await())
}

/**
 * Runs [fa], [fb], [fc], [fd] in parallel on [Dispatchers.Default] and combines their results using the provided function.
 *
 * ```kotlin:ank:playground
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
 *
 * @param fa value to parallel map
 * @param fb value to parallel map
 * @param fc value to parallel map
 * @param fd value to parallel map
 * @param f function to map/combine value [A], [B], [C] and [D]
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
 * ```kotlin:ank:playground
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
 *
 * @param fa value to parallel map
 * @param fb value to parallel map
 * @param fc value to parallel map
 * @param fd value to parallel map
 * @param f function to map/combine value [A], [B], [C] and [D].
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
  val a = async(ctx) { fa() }
  val b = async(ctx) { fb() }
  val c = async(ctx) { fc() }
  val d = async(ctx) { fd() }
  f(a.await(), b.await(), c.await(), d.await())
}

/**
 * Runs [fa], [fb], [fc], [fd], [fe] in parallel on [Dispatchers.Default] and combines
 * their results using the provided function.
 *
 * ```kotlin:ank:playground
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
 *
 * @param fa value to parallel map
 * @param fb value to parallel map
 * @param fc value to parallel map
 * @param fd value to parallel map
 * @param fe value to parallel map
 * @param f function to map/combine value [A], [B], [C], [D] and [E]
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
 * ```kotlin:ank:playground
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
 *
 * @param fa value to parallel map
 * @param fb value to parallel map
 * @param fc value to parallel map
 * @param fd value to parallel map
 * @param fe value to parallel map
 * @param f function to map/combine value [A], [B], [C], [D], and [E].
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
  val a = async(ctx) { fa() }
  val b = async(ctx) { fb() }
  val c = async(ctx) { fc() }
  val d = async(ctx) { fd() }
  val e = async(ctx) { fe() }
  f(a.await(), b.await(), c.await(), d.await(), e.await())
}

/**
 * Runs [fa], [fb], [fc], [fd], [fe], [ff] in parallel on [Dispatchers.Default] and combines
 * their results using the provided function.
 *
 * ```kotlin:ank:playground
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
 *
 * @param fa value to parallel map
 * @param fb value to parallel map
 * @param fc value to parallel map
 * @param fd value to parallel map
 * @param fe value to parallel map
 * @param ff value to parallel map
 * @param f function to map/combine value [A], [B], [C], [D], [E] and [F]
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
 * ```kotlin:ank:playground
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
 *
 * @param fa value to parallel map
 * @param fb value to parallel map
 * @param fc value to parallel map
 * @param fd value to parallel map
 * @param fe value to parallel map
 * @param ff value to parallel map
 * @param f function to map/combine value [A], [B], [C], [D], [E] and [F]
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
  val a = async(ctx) { fa() }
  val b = async(ctx) { fb() }
  val c = async(ctx) { fc() }
  val d = async(ctx) { fd() }
  val e = async(ctx) { fe() }
  val g = async(ctx) { ff() }
  f(a.await(), b.await(), c.await(), d.await(), e.await(), g.await())
}

/**
 * Runs [fa], [fb], [fc], [fd], [fe], [ff], [fg] in parallel on [Dispatchers.Default] and combines
 * their results using the provided function.
 *
 * ```kotlin:ank:playground
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
 *
 * @param fa value to parallel map
 * @param fb value to parallel map
 * @param fc value to parallel map
 * @param fd value to parallel map
 * @param fe value to parallel map
 * @param ff value to parallel map
 * @param fg value to parallel map
 * @param f function to map/combine value [A], [B], [C], [D], [E], [F] and [G]
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
 * ```kotlin:ank:playground
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
 *
 * @param fa value to parallel map
 * @param fb value to parallel map
 * @param fc value to parallel map
 * @param fd value to parallel map
 * @param fe value to parallel map
 * @param ff value to parallel map
 * @param fg value to parallel map
 * @param f function to map/combine value [A], [B], [C], [D], [E], [F] and [G]
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
  val a = async(ctx) { fa() }
  val b = async(ctx) { fb() }
  val c = async(ctx) { fc() }
  val d = async(ctx) { fd() }
  val e = async(ctx) { fe() }
  val fDef = async(ctx) { ff() }
  val g = async(ctx) { fg() }
  f(a.await(), b.await(), c.await(), d.await(), e.await(), fDef.await(), g.await())
}

/**
 * Runs [fa], [fb], [fc], [fd], [fe], [ff], [fg], [fh] in parallel on [Dispatchers.Default] and combines
 * their results using the provided function.
 *
 * ```kotlin:ank:playground
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
 *
 * @param fa value to parallel map
 * @param fb value to parallel map
 * @param fc value to parallel map
 * @param fd value to parallel map
 * @param fe value to parallel map
 * @param ff value to parallel map
 * @param fg value to parallel map
 * @param fh value to parallel map
 * @param f function to map/combine value [A], [B], [C], [D], [E], [F], [G] and [H]
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
 * ```kotlin:ank:playground
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
 *
 * @param fa value to parallel map
 * @param fb value to parallel map
 * @param fc value to parallel map
 * @param fd value to parallel map
 * @param fe value to parallel map
 * @param ff value to parallel map
 * @param fg value to parallel map
 * @param fh value to parallel map
 * @param f function to map/combine value [A], [B], [C], [D], [E], [F], [G] and [H]
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
  val a = async(ctx) { fa() }
  val b = async(ctx) { fb() }
  val c = async(ctx) { fc() }
  val d = async(ctx) { fd() }
  val e = async(ctx) { fe() }
  val fDef = async(ctx) { ff() }
  val g = async(ctx) { fg() }
  val h = async(ctx) { fh() }
  f(a.await(), b.await(), c.await(), d.await(), e.await(), fDef.await(), g.await(), h.await())
}
