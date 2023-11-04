package arrow.collectors

import arrow.fx.coroutines.parMap
import arrow.fx.coroutines.parMapUnordered
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.DEFAULT_CONCURRENCY
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine

/**
 * Performs collection over the elements of [this].
 * The amount of concurrency depends on the
 * [Characteristics] of [collector], and can
 * be tweaked using the [concurrency] parameter.
 *
 * [this] is iterated only once during collection.
 *
 * Note: if you need to perform changes on the values
 * before collection, we strongly recommend to convert
 * the [Iterable] into a [Flow] using [asFlow],
 * perform those changes, and then using [collect].
 *
 * @receiver Sequence of values to collect
 * @param collector Describes how to collect the values
 * @param concurrency Defines the concurrency limit
 */
@OptIn(FlowPreview::class)
public suspend fun <T, R> Iterable<T>.collect(
  collector: Collector<T, R>,
  concurrency: Int = DEFAULT_CONCURRENCY,
): R = asFlow().collect(collector, concurrency)

/**
 * Performs collection over the elements of [this].
 * The amount of concurrency depends on the
 * [Characteristics] of [collector], and can
 * be tweaked using the [concurrency] parameter.
 *
 * [this] is consumed only once during collection.
 * We recommend using a cold [Flow] to ensure that
 * elements are produced only when needed.
 *
 * @receiver [Flow] of elements to collect
 * @param collector Describes how to collect the values
 * @param concurrency Defines the concurrency limit
 */
@OptIn(FlowPreview::class)
public suspend fun <T, R> Flow<T>.collect(
  collector: Collector<T, R>,
  concurrency: Int = DEFAULT_CONCURRENCY,
): R = collectI(collector, concurrency)

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@Suppress("UNINITIALIZED_VARIABLE", "UNCHECKED_CAST")
internal suspend fun <A, T, R> Flow<T>.collectI(
  collector: CollectorI<A, T, R>,
  concurrency: Int = DEFAULT_CONCURRENCY,
): R {
  var result: A
  val started = this.onStart { result = collector.supply() }
  val continued = when {
    Characteristics.CONCURRENT in collector.characteristics -> when {
      Characteristics.UNORDERED in collector.characteristics ->
        started.parMapUnordered(concurrency) { collector.accumulate(result, it) }

      else ->
        started.parMap(concurrency) { collector.accumulate(result, it) }
    }

    else -> started.map { collector.accumulate(result, it) }
  }
  var completed: R
  continued.onCompletion {
    completed = when {
      Characteristics.IDENTITY_FINISH in collector.characteristics -> result as R
      else -> collector.finish(result)
    }
  }.collect { }
  return completed
}
