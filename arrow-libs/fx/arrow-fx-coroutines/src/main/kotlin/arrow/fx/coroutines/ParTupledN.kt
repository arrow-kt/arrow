package arrow.fx.coroutines

import kotlin.coroutines.CoroutineContext

/**
 * Tuples [fa], [fb] in parallel on [ComputationPool].
 * Cancelling this operation cancels both operations running in parallel.
 *
 * @see parTupledN for the same function that can race on any [CoroutineContext].
 */
suspend fun <A, B> parTupledN(fa: suspend () -> A, fb: suspend () -> B): Pair<A, B> =
  parTupledN(ComputationPool, fa, fb)

/**
 * Tuples [fa], [fb], [fc] in parallel on [ComputationPool].
 * Cancelling this operation cancels both tasks running in parallel.
 *
 * @see parTupledN for the same function that can race on any [CoroutineContext].
 */
suspend fun <A, B, C> parTupledN(fa: suspend () -> A, fb: suspend () -> B, fc: suspend () -> C): Triple<A, B, C> =
  parTupledN(ComputationPool, fa, fb, fc)

/**
 * Tuples [fa], [fb] on the provided [CoroutineContext].
 * Cancelling this operation cancels both tasks running in parallel.
 *
 * **WARNING**: operations run in parallel depending on the capabilities of the provided [CoroutineContext].
 * We ensure they start in sequence so it's guaranteed to finish on a single threaded context.
 *
 * @see parTupledN for a function that ensures it runs in parallel on the [ComputationPool].
 */
suspend fun <A, B> parTupledN(ctx: CoroutineContext, fa: suspend () -> A, fb: suspend () -> B): Pair<A, B> =
  parMapN(ctx, fa, fb, ::Pair)

/**
 * Tuples [fa], [fb] & [fc] on the provided [CoroutineContext].
 * Cancelling this operation cancels both tasks running in parallel.
 *
 * **WARNING**: operations run in parallel depending on the capabilities of the provided [CoroutineContext].
 * We ensure they start in sequence so it's guaranteed to finish on a single threaded context.
 *
 * @see parTupledN for a function that ensures it runs in parallel on the [ComputationPool].
 */
suspend fun <A, B, C> parTupledN(ctx: CoroutineContext, fa: suspend () -> A, fb: suspend () -> B, fc: suspend () -> C): Triple<A, B, C> =
  parMapN(ctx, fa, fb, fc, ::Triple)
