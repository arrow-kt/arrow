package arrow.fx.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
@Deprecated(
  "parMapN has been deprecated in favor of parZip with CoroutineScope enabled lambdas",
  ReplaceWith(
    "parZip({ fa() }, { fb() }) { a, b -> f(a, b) }",
    "arrow.core.parZip"
  )
)
suspend inline fun <A, B, C> parMapN(
  crossinline fa: suspend () -> A,
  crossinline fb: suspend () -> B,
  crossinline f: suspend (A, B) -> C
): C = parZip(Dispatchers.Default, { fa() }, { fb() }) { a, b -> f(a, b) }

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
@Deprecated(
  "parMapN has been deprecated in favor of parZip with CoroutineScope enabled lambdas",
  ReplaceWith(
    "parZip(ctx, { fa() }, { fb() }) { a, b -> f(a, b) }",
    "arrow.core.parZip"
  )
)
suspend inline fun <A, B, C> parMapN(
  ctx: CoroutineContext = EmptyCoroutineContext,
  crossinline fa: suspend () -> A,
  crossinline fb: suspend () -> B,
  crossinline f: suspend (A, B) -> C
): C =
  parZip(ctx, { fa() }, { fb() }) { a, b -> f(a, b) }

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
@Deprecated(
  "parMapN has been deprecated in favor of parZip with CoroutineScope enabled lambdas",
  ReplaceWith(
    "parZip({ fa() }, { fb() }, { fc() }) { a, b, c -> f(a, b, c) }",
    "arrow.core.parZip"
  )
)
suspend inline fun <A, B, C, D> parMapN(
  crossinline fa: suspend () -> A,
  crossinline fb: suspend () -> B,
  crossinline fc: suspend () -> C,
  crossinline f: suspend (A, B, C) -> D
): D =
  parZip(Dispatchers.Default, { fa() }, { fb() }, { fc() }) { a, b, c -> f(a, b, c) }

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
@Deprecated(
  "parMapN has been deprecated in favor of parZip with CoroutineScope enabled lambdas",
  ReplaceWith(
    "parZip(ctx, { fa() }, { fb() }, { fc() }) { a, b, c -> f(a, b, c) }",
    "arrow.core.parZip"
  )
)
suspend inline fun <A, B, C, D> parMapN(
  ctx: CoroutineContext = EmptyCoroutineContext,
  crossinline fa: suspend () -> A,
  crossinline fb: suspend () -> B,
  crossinline fc: suspend () -> C,
  crossinline f: suspend (A, B, C) -> D
): D =
  parZip(ctx, { fa() }, { fb() }, { fc() }) { a, b, c ->
    f(a, b, c)
  }

/**
 * Runs [fa], [fb], [fc], [fd] in parallel on [Dispatchers.Default] and combines their results using the provided function.
 *
 * ```kotlin:ank:playground
 * import arrow.fx.coroutines.*
 *
 * suspend fun main(): Unit {
 *   //sampleStart
 *   val result = parMapN(
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
 * @see parMapN for a function that can run on any [CoroutineContext].
 */
@Deprecated(
  "parMapN has been deprecated in favor of parZip with CoroutineScope enabled lambdas",
  ReplaceWith(
    "parZip(Dispatchers.Default, { fa() }, { fb() }, { fc() }, { fd() }) { a, b, c, d -> f(a, b, c, d) }",
    "arrow.core.parZip"
  )
)
suspend inline fun <A, B, C, D, E> parMapN(
  crossinline fa: suspend () -> A,
  crossinline fb: suspend () -> B,
  crossinline fc: suspend () -> C,
  crossinline fd: suspend () -> D,
  crossinline f: suspend (A, B, C, D) -> E
): E =
  parZip({ fa() }, { fb() }, { fc() }, { fd() }) { a, b, c, d -> f(a, b, c, d) }

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
 *   val result = parMapN(
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
 * @see parMapN for a function that ensures operations run in parallel on the [Dispatchers.Default].
 */
@Deprecated(
  "parMapN has been deprecated in favor of parZip with CoroutineScope enabled lambdas",
  ReplaceWith(
    "parZip(ctx, { fa() }, { fb() }, { fc() }, { fd() }) { a, b, c, d -> f(a, b, c, d) }",
    "arrow.core.parZip"
  )
)
suspend inline fun <A, B, C, D, E> parMapN(
  ctx: CoroutineContext = EmptyCoroutineContext,
  crossinline fa: suspend () -> A,
  crossinline fb: suspend () -> B,
  crossinline fc: suspend () -> C,
  crossinline fd: suspend () -> D,
  crossinline f: suspend (A, B, C, D) -> E
): E =
  parZip(ctx, { fa() }, { fb() }, { fc() }, { fd() }) { a, b, c, d ->
    f(a, b, c, d)
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
 *   val result = parMapN(
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
 * @see parMapN for a function that can run on any [CoroutineContext].
 */
@Deprecated(
  "parMapN has been deprecated in favor of parZip with CoroutineScope enabled lambdas",
  ReplaceWith(
    "parZip({ fa() }, { fb() }, { fc() }, { fd() }, { fe() }) { a, b, c, d, e -> f(a, b, c, d, e) }",
    "arrow.core.parZip"
  )
)
suspend inline fun <A, B, C, D, E, F> parMapN(
  crossinline fa: suspend () -> A,
  crossinline fb: suspend () -> B,
  crossinline fc: suspend () -> C,
  crossinline fd: suspend () -> D,
  crossinline fe: suspend () -> E,
  crossinline f: suspend (A, B, C, D, E) -> F
): F =
  parZip(Dispatchers.Default, { fa() }, { fb() }, { fc() }, { fd() }, { fe() }) { a, b, c, d, e -> f(a, b, c, d, e) }

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
 *   val result = parMapN(
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
 * @see parMapN for a function that ensures operations run in parallel on the [Dispatchers.Default].
 */
@Deprecated(
  "parMapN has been deprecated in favor of parZip with CoroutineScope enabled lambdas",
  ReplaceWith(
    "parZip(ctx, { fa() }, { fb() }, { fc() }, { fd() }, { fe() }) { a, b, c, d, e -> f(a, b, c, d, e) }",
    "arrow.core.parZip"
  )
)
suspend inline fun <A, B, C, D, E, F> parMapN(
  ctx: CoroutineContext = EmptyCoroutineContext,
  crossinline fa: suspend () -> A,
  crossinline fb: suspend () -> B,
  crossinline fc: suspend () -> C,
  crossinline fd: suspend () -> D,
  crossinline fe: suspend () -> E,
  crossinline f: suspend (A, B, C, D, E) -> F
): F =
  parZip(ctx, { fa() }, { fb() }, { fc() }, { fd() }, { fe() }) { a, b, c, d, e ->
    f(a, b, c, d, e)
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
 *   val result = parMapN(
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
 * @see parMapN for a function that can run on any [CoroutineContext].
 */
@Deprecated(
  "parMapN has been deprecated in favor of parZip with CoroutineScope enabled lambdas",
  ReplaceWith(
    "parZip({ fa() }, { fb() }, { fc() }, { fd() }, { fe() }, { ff() }) { a, b, c, d, e, ff -> f(a, b, c, d, e, ff) }",
    "arrow.core.parZip"
  )
)
suspend inline fun <A, B, C, D, E, F, G> parMapN(
  crossinline fa: suspend () -> A,
  crossinline fb: suspend () -> B,
  crossinline fc: suspend () -> C,
  crossinline fd: suspend () -> D,
  crossinline fe: suspend () -> E,
  crossinline ff: suspend () -> F,
  crossinline f: suspend (A, B, C, D, E, F) -> G
): G =
  parZip(Dispatchers.Default, { fa() }, { fb() }, { fc() }, { fd() }, { fe() }, { ff() }) { a, b, c, d, e, ff ->
    f(
      a,
      b,
      c,
      d,
      e,
      ff
    )
  }

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
 *   val result = parMapN(
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
 * @see parMapN for a function that ensures operations run in parallel on the [Dispatchers.Default].
 */
@Deprecated(
  "parMapN has been deprecated in favor of parZip with CoroutineScope enabled lambdas",
  ReplaceWith(
    "parZip(ctx, { fa() }, { fb() }, { fc() }, { fd() }, { fe() }, { ff() }) { a, b, c, d, e, ff -> f(a, b, c, d, e, ff) }",
    "arrow.core.parZip"
  )
)
suspend inline fun <A, B, C, D, E, F, G> parMapN(
  ctx: CoroutineContext = EmptyCoroutineContext,
  crossinline fa: suspend () -> A,
  crossinline fb: suspend () -> B,
  crossinline fc: suspend () -> C,
  crossinline fd: suspend () -> D,
  crossinline fe: suspend () -> E,
  crossinline ff: suspend () -> F,
  crossinline f: suspend (A, B, C, D, E, F) -> G
): G =
  parZip(ctx, { fa() }, { fb() }, { fc() }, { fd() }, { fe() }, { ff() }) { a, b, c, d, e, ff ->
    f(a, b, c, d, e, ff)
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
 *   val result = parMapN(
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
 * @see parMapN for a function that can run on any [CoroutineContext].
 */
@Deprecated(
  "parMapN has been deprecated in favor of parZip with CoroutineScope enabled lambdas",
  ReplaceWith(
    "parZip({ fa() }, { fb() }, { fc() }, { fd() }, { fe() }, { ff() }, { fg() }) { a, b, c, d, e, ff, g -> f(a, b, c, d, e, ff, g) }",
    "arrow.core.parZip"
  )
)
suspend inline fun <A, B, C, D, E, F, G, H> parMapN(
  crossinline fa: suspend () -> A,
  crossinline fb: suspend () -> B,
  crossinline fc: suspend () -> C,
  crossinline fd: suspend () -> D,
  crossinline fe: suspend () -> E,
  crossinline ff: suspend () -> F,
  crossinline fg: suspend () -> G,
  crossinline f: suspend (A, B, C, D, E, F, G) -> H
): H =
  parZip(
    Dispatchers.Default,
    { fa() },
    { fb() },
    { fc() },
    { fd() },
    { fe() },
    { ff() },
    { fg() }) { a, b, c, d, e, ff, g -> f(a, b, c, d, e, ff, g) }

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
 *   val result = parMapN(
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
 * @see parMapN for a function that ensures operations run in parallel on the [Dispatchers.Default].
 */
@Deprecated(
  "parMapN has been deprecated in favor of parZip with CoroutineScope enabled lambdas",
  ReplaceWith(
    "parZip(ctx, { fa() }, { fb() }, { fc() }, { fd() }, { fe() }, { ff() }, { fg() }) { a, b, c, d, e, ff, g -> f(a, b, c, d, e, ff, g) }",
    "arrow.core.parZip"
  )
)
suspend inline fun <A, B, C, D, E, F, G, H> parMapN(
  ctx: CoroutineContext = EmptyCoroutineContext,
  crossinline fa: suspend () -> A,
  crossinline fb: suspend () -> B,
  crossinline fc: suspend () -> C,
  crossinline fd: suspend () -> D,
  crossinline fe: suspend () -> E,
  crossinline ff: suspend () -> F,
  crossinline fg: suspend () -> G,
  crossinline f: suspend (A, B, C, D, E, F, G) -> H
): H =
  parZip(ctx, { fa() }, { fb() }, { fc() }, { fd() }, { fe() }, { ff() }, { fg() }) { a, b, c, d, e, ff, g ->
    f(a, b, c, d, e, ff, g)
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
 *   val result = parMapN(
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
 * @see parMapN for a function that can run on any [CoroutineContext].
 */
@Deprecated(
  "parMapN has been deprecated in favor of parZip with CoroutineScope enabled lambdas",
  ReplaceWith(
    "parZip({ fa() }, { fb() }, { fc() }, { fd() }, { fe() }, { ff() }, { fg() }, { fh() }) { a, b, c, d, e, f, g, h -> f(a, b, c, d, e, f, g, h) }",
    "arrow.core.parZip"
  )
)
suspend inline fun <A, B, C, D, E, F, G, H, I> parMapN(
  crossinline fa: suspend () -> A,
  crossinline fb: suspend () -> B,
  crossinline fc: suspend () -> C,
  crossinline fd: suspend () -> D,
  crossinline fe: suspend () -> E,
  crossinline ff: suspend () -> F,
  crossinline fg: suspend () -> G,
  crossinline fh: suspend () -> H,
  crossinline f: suspend (A, B, C, D, E, F, G, H) -> I
): I =
  parZip(
    Dispatchers.Default,
    { fa() },
    { fb() },
    { fc() },
    { fd() },
    { fe() },
    { ff() },
    { fg() },
    { fh() }) { a, b, c, d, e, f, g, h -> f(a, b, c, d, e, f, g, h) }

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
 *   val result = parMapN(
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
 * @see parMapN for a function that ensures operations run in parallel on the [Dispatchers.Default].
 */
@Deprecated(
  "parMapN has been deprecated in favor of parZip with CoroutineScope enabled lambdas",
  ReplaceWith(
    "parZip(ctx, { fa() }, { fb() }, { fc() }, { fd() }, { fe() }, { ff() }, { fg() }, { fh() }) { a, b, c, d, e, f, g, h -> f(a, b, c, d, e, f, g, h) }",
    "arrow.core.parZip"
  )
)
suspend inline fun <A, B, C, D, E, F, G, H, I> parMapN(
  ctx: CoroutineContext = EmptyCoroutineContext,
  crossinline fa: suspend () -> A,
  crossinline fb: suspend () -> B,
  crossinline fc: suspend () -> C,
  crossinline fd: suspend () -> D,
  crossinline fe: suspend () -> E,
  crossinline ff: suspend () -> F,
  crossinline fg: suspend () -> G,
  crossinline fh: suspend () -> H,
  crossinline f: suspend (A, B, C, D, E, F, G, H) -> I
): I =
  parZip(
    ctx,
    { fa() },
    { fb() },
    { fc() },
    { fd() },
    { fe() },
    { ff() },
    { fg() },
    { fh() }) { a, b, c, d, e, ff, g, h ->
    f(a, b, c, d, e, ff, g, h)
  }
