package arrow.fx.coroutines

import arrow.core.*
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Runs [fa], [fb] in parallel on [ctx] and combines their results as a tuple.
 *
 * ```kotlin
 * import arrow.fx.coroutines.*
 * import kotlinx.coroutines.Dispatchers
 *
 * suspend fun main(): Unit {
 *   //sampleStart
 *   val result = parTupled(
 *     Dispatchers.IO,
 *     { "First one is on ${Thread.currentThread().name}" },
 *     { "Second one is on ${Thread.currentThread().name}" }
 *   )
 *   //sampleEnd
 *  println(result)
 * }
 * ```
 * <!--- KNIT example-partupled-01.kt -->
 *
 * @param fa value to parallel tuple
 * @param fb value to parallel tuple
 */
public suspend inline fun <A, B> parTupled(
  context: CoroutineContext = EmptyCoroutineContext,
  crossinline fa: suspend CoroutineScope.() -> A,
  crossinline fb: suspend CoroutineScope.() -> B,
):  Pair<A, B> = parZip(context, fa, fb) { a, b ->
  Pair(a, b)
}

/**
 * Runs [fa], [fb], [fc] in parallel on [ctx] and combines their results as a tuple.
 *
 * ```kotlin
 * import arrow.fx.coroutines.*
 * import kotlinx.coroutines.Dispatchers
 *
 * suspend fun main(): Unit {
 *   //sampleStart
 *   val result: Triple<String, String, String> = parTupled(
 *     Dispatchers.IO,
 *     { "First one is on ${Thread.currentThread().name}" },
 *     { "Second one is on ${Thread.currentThread().name}" },
 *     { "Third one is on ${Thread.currentThread().name}" }
 *   )
 *   //sampleEnd
 *  println(result)
 * }
 * ```
 * <!--- KNIT example-partupled-02.kt -->
 *
 * @param fa value to parallel tuple
 * @param fb value to parallel tuple
 * @param fc value to parallel tuple
 */
public suspend inline fun <A, B, C> parTupled(
  context: CoroutineContext = EmptyCoroutineContext,
  crossinline fa: suspend CoroutineScope.() -> A,
  crossinline fb: suspend CoroutineScope.() -> B,
  crossinline fc: suspend CoroutineScope.() -> C,
):  Triple<A, B, C> = parZip(context, fa, fb, fc) { a, b, c ->
  Triple(a, b, c)
}

/**
 * Runs [fa], [fb], [fc], [fd] in parallel on [ctx] and combines their results as a tuple.
 *
 * ```kotlin
 * import arrow.fx.coroutines.*
 * import kotlinx.coroutines.Dispatchers
 *
 * suspend fun main(): Unit {
 *   //sampleStart
 *   val result = parTupled(
 *     Dispatchers.IO,
 *     { "First one is on ${Thread.currentThread().name}" },
 *     { "Second one is on ${Thread.currentThread().name}" },
 *     { "Third one is on ${Thread.currentThread().name}" },
 *     { "Fourth one is on ${Thread.currentThread().name}" }
 *   )
 *   //sampleEnd
 *  println(result)
 * }
 * ```
 * <!--- KNIT example-partupled-03.kt -->
 *
 * @param fa value to parallel tuple
 * @param fb value to parallel tuple
 * @param fc value to parallel tuple
 * @param fd value to parallel tuple
 */
public suspend inline fun <A, B, C, D> parTupled(
  context: CoroutineContext = EmptyCoroutineContext,
  crossinline fa: suspend CoroutineScope.() -> A,
  crossinline fb: suspend CoroutineScope.() -> B,
  crossinline fc: suspend CoroutineScope.() -> C,
  crossinline fd: suspend CoroutineScope.() -> D,
): Tuple4<A, B, C, D> = parZip(context, fa, fb, fc, fd) { a, b, c, d->
  Tuple4(a, b, c, d)
}

/**
 * Runs [fa], [fb], [fc], [fd], [fe] in parallel on [ctx] and combines their results as a tuple.
 *
 * ```kotlin
 * import arrow.fx.coroutines.*
 * import kotlinx.coroutines.Dispatchers
 *
 * suspend fun main(): Unit {
 *   //sampleStart
 *   val result = parTupled(
 *     Dispatchers.IO,
 *     { "First one is on ${Thread.currentThread().name}" },
 *     { "Second one is on ${Thread.currentThread().name}" },
 *     { "Third one is on ${Thread.currentThread().name}" },
 *     { "Fourth one is on ${Thread.currentThread().name}" },
 *     { "Fifth one is on ${Thread.currentThread().name}" }
 *   )
 *   //sampleEnd
 *  println(result)
 * }
 * ```
 * <!--- KNIT example-partupled-04.kt -->
 *
 * @param fa value to parallel tuple
 * @param fb value to parallel tuple
 * @param fc value to parallel tuple
 * @param fd value to parallel tuple
 * @param fe value to parallel tuple
 */
public suspend inline fun <A, B, C, D, E> parTupled(
  context: CoroutineContext = EmptyCoroutineContext,
  crossinline fa: suspend CoroutineScope.() -> A,
  crossinline fb: suspend CoroutineScope.() -> B,
  crossinline fc: suspend CoroutineScope.() -> C,
  crossinline fd: suspend CoroutineScope.() -> D,
  crossinline fe: suspend CoroutineScope.() -> E,
): Tuple5<A, B, C, D, E> = parZip(context, fa, fb, fc, fd, fe) { a, b, c, d, e->
  Tuple5(a, b, c, d, e)
}

/**
 * Runs [fa], [fb], [fc], [fd], [fe], [ff] in parallel on [ctx] and combines their results as a tuple.
 *
 * ```kotlin
 * import arrow.fx.coroutines.*
 * import kotlinx.coroutines.Dispatchers
 *
 * suspend fun main(): Unit {
 *   //sampleStart
 *   val result = parTupled(
 *     Dispatchers.IO,
 *     { "First one is on ${Thread.currentThread().name}" },
 *     { "Second one is on ${Thread.currentThread().name}" },
 *     { "Third one is on ${Thread.currentThread().name}" },
 *     { "Fourth one is on ${Thread.currentThread().name}" },
 *     { "Fifth one is on ${Thread.currentThread().name}" },
 *     { "Sixth one is on ${Thread.currentThread().name}" }
 *   )
 *   //sampleEnd
 *  println(result)
 * }
 * ```
 * <!--- KNIT example-partupled-05.kt -->
 *
 * @param fa value to parallel tuple
 * @param fb value to parallel tuple
 * @param fc value to parallel tuple
 * @param fd value to parallel tuple
 * @param fe value to parallel tuple
 * @param ff value to parallel tuple
 */
public suspend inline fun <A, B, C, D, E, F> parTupled(
  context: CoroutineContext = EmptyCoroutineContext,
  crossinline fa: suspend CoroutineScope.() -> A,
  crossinline fb: suspend CoroutineScope.() -> B,
  crossinline fc: suspend CoroutineScope.() -> C,
  crossinline fd: suspend CoroutineScope.() -> D,
  crossinline fe: suspend CoroutineScope.() -> E,
  crossinline ff: suspend CoroutineScope.() -> F,
): Tuple6<A, B, C, D, E, F> = parZip(context, fa, fb, fc, fd, fe, ff) { a, b, c, d, e, f->
  Tuple6(a, b, c, d, e, f)
}

/**
 * Runs [fa], [fb], [fc], [fd], [fe], [ff], [fg] in parallel on [ctx] and combines their results as a tuple.
 *
 * ```kotlin
 * import arrow.fx.coroutines.*
 * import kotlinx.coroutines.Dispatchers
 *
 * suspend fun main(): Unit {
 *   //sampleStart
 *   val result = parTupled(
 *     Dispatchers.IO,
 *     { "First one is on ${Thread.currentThread().name}" },
 *     { "Second one is on ${Thread.currentThread().name}" },
 *     { "Third one is on ${Thread.currentThread().name}" },
 *     { "Fourth one is on ${Thread.currentThread().name}" },
 *     { "Fifth one is on ${Thread.currentThread().name}" },
 *     { "Sixth one is on ${Thread.currentThread().name}" },
 *     { "Seventh one is on ${Thread.currentThread().name}" },
 *   )
 *   //sampleEnd
 *  println(result)
 * }
 * ```
 * <!--- KNIT example-partupled-06.kt -->
 *
 * @param fa value to parallel tuple
 * @param fb value to parallel tuple
 * @param fc value to parallel tuple
 * @param fd value to parallel tuple
 * @param fe value to parallel tuple
 * @param ff value to parallel tuple
 * @param fg value to parallel tuple
 */
public suspend inline fun <A, B, C, D, E, F, G> parTupled(
  context: CoroutineContext = EmptyCoroutineContext,
  crossinline fa: suspend CoroutineScope.() -> A,
  crossinline fb: suspend CoroutineScope.() -> B,
  crossinline fc: suspend CoroutineScope.() -> C,
  crossinline fd: suspend CoroutineScope.() -> D,
  crossinline fe: suspend CoroutineScope.() -> E,
  crossinline ff: suspend CoroutineScope.() -> F,
  crossinline fg: suspend CoroutineScope.() -> G,
): Tuple7<A, B, C, D, E, F, G> = parZip(context, fa, fb, fc, fd, fe, ff, fg) { a, b, c, d, e, f, g->
  Tuple7(a, b, c, d, e, f, g)
}

/**
 * Runs [fa], [fb], [fc], [fd], [fe], [ff], [fg], [fh] in parallel on [ctx] and combines their results as a tuple.
 *
 * ```kotlin
 * import arrow.fx.coroutines.*
 * import kotlinx.coroutines.Dispatchers
 *
 * suspend fun main(): Unit {
 *   //sampleStart
 *   val result = parTupled(
 *     Dispatchers.IO,
 *     { "First one is on ${Thread.currentThread().name}" },
 *     { "Second one is on ${Thread.currentThread().name}" },
 *     { "Third one is on ${Thread.currentThread().name}" },
 *     { "Fourth one is on ${Thread.currentThread().name}" },
 *     { "Fifth one is on ${Thread.currentThread().name}" },
 *     { "Sixth one is on ${Thread.currentThread().name}" },
 *     { "Seventh one is on ${Thread.currentThread().name}" },
 *     { "Eighth one is on ${Thread.currentThread().name}" }
 *   )
 *   //sampleEnd
 *  println(result)
 * }
 * ```
 * <!--- KNIT example-partupled-07.kt -->
 *
 * @param fa value to parallel tuple
 * @param fb value to parallel tuple
 * @param fc value to parallel tuple
 * @param fd value to parallel tuple
 * @param fe value to parallel tuple
 * @param ff value to parallel tuple
 * @param fg value to parallel tuple
 * @param fh value to parallel tuple
 */
public suspend inline fun <A, B, C, D, E, F, G, H> parTupled(
  context: CoroutineContext = EmptyCoroutineContext,
  crossinline fa: suspend CoroutineScope.() -> A,
  crossinline fb: suspend CoroutineScope.() -> B,
  crossinline fc: suspend CoroutineScope.() -> C,
  crossinline fd: suspend CoroutineScope.() -> D,
  crossinline fe: suspend CoroutineScope.() -> E,
  crossinline ff: suspend CoroutineScope.() -> F,
  crossinline fg: suspend CoroutineScope.() -> G,
  crossinline fh: suspend CoroutineScope.() -> H,
): Tuple8<A, B, C, D, E, F, G, H> = parZip(context, fa, fb, fc, fd, fe, ff, fg,  fh) { a, b, c, d, e, f, g, h->
  Tuple8(a, b, c, d, e, f, g, h)
}

/**
 * Runs [fa], [fb], [fc], [fd], [fe], [ff], [fg], [fh], [fi] in parallel on [ctx] and combines their results as a tuple.
 *
 * ```kotlin
 * import arrow.fx.coroutines.*
 * import kotlinx.coroutines.Dispatchers
 *
 * suspend fun main(): Unit {
 *   //sampleStart
 *   val result = parTupled(
 *     Dispatchers.IO,
 *     { "First one is on ${Thread.currentThread().name}" },
 *     { "Second one is on ${Thread.currentThread().name}" },
 *     { "Third one is on ${Thread.currentThread().name}" },
 *     { "Fourth one is on ${Thread.currentThread().name}" },
 *     { "Fifth one is on ${Thread.currentThread().name}" },
 *     { "Sixth one is on ${Thread.currentThread().name}" },
 *     { "Seventh one is on ${Thread.currentThread().name}" },
 *     { "Eighth one is on ${Thread.currentThread().name}" },
 *     { "Ninth one is on ${Thread.currentThread().name}" }
 *   )
 *   //sampleEnd
 *  println(result)
 * }
 * ```
 * <!--- KNIT example-partupled-08.kt -->
 *
 * @param fa value to parallel tuple
 * @param fb value to parallel tuple
 * @param fc value to parallel tuple
 * @param fd value to parallel tuple
 * @param fe value to parallel tuple
 * @param ff value to parallel tuple
 * @param fg value to parallel tuple
 * @param fh value to parallel tuple
 * @param fi value to parallel tuple
 */
public suspend inline fun <A, B, C, D, E, F, G, H, I> parTupled(
  context: CoroutineContext = EmptyCoroutineContext,
  crossinline fa: suspend CoroutineScope.() -> A,
  crossinline fb: suspend CoroutineScope.() -> B,
  crossinline fc: suspend CoroutineScope.() -> C,
  crossinline fd: suspend CoroutineScope.() -> D,
  crossinline fe: suspend CoroutineScope.() -> E,
  crossinline ff: suspend CoroutineScope.() -> F,
  crossinline fg: suspend CoroutineScope.() -> G,
  crossinline fh: suspend CoroutineScope.() -> H,
  crossinline fi: suspend CoroutineScope.() -> I,
): Tuple9<A, B, C, D, E, F, G, H, I> = parZip(context, fa, fb, fc, fd, fe, ff, fg,  fh, fi) { a, b, c, d, e, f, g, h, i->
  Tuple9(a, b, c, d, e, f, g, h, i)
} 
