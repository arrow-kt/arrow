package arrow.collectors

import arrow.fx.coroutines.parMap
import arrow.fx.coroutines.parMapUnordered
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.DEFAULT_CONCURRENCY
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart

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
public suspend fun <T, R> Iterable<T>.parCollect(
  collector: Collector<T, R>,
  concurrency: Int = DEFAULT_CONCURRENCY,
): R = asFlow().collect(collector, concurrency)

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
 * the [Sequence] into a [Flow] using [asFlow],
 * perform those changes, and then using [collect].
 *
 * @receiver Sequence of values to collect
 * @param collector Describes how to collect the values
 * @param concurrency Defines the concurrency limit
 */
@OptIn(FlowPreview::class)
public suspend fun <T, R> Sequence<T>.parCollect(
  collector: Collector<T, R>,
  concurrency: Int = DEFAULT_CONCURRENCY,
): R = asFlow().collect(collector, concurrency)

/**
 * Performs collection over the elements of [this]
 * in a non-concurrent fashion.
 *
 * [this] is iterated only once during collection.
 *
 * @receiver Sequence of values to collect
 * @param collector Describes how to collect the values
 */
public fun <T, R> Iterable<T>.collect(
  collector: NonSuspendCollector<T, R>
): R = iterator().collectI(collector)

/**
 * Performs collection over the elements of [this]
 * in a non-concurrent fashion.
 *
 * [this] is iterated only once during collection.
 *
 * @receiver Sequence of values to collect
 * @param collector Describes how to collect the values
 */
public fun <T, R> Sequence<T>.collect(
  collector: NonSuspendCollector<T, R>
): R = iterator().collectI(collector)

public fun <T, R> Iterator<T>.collect(
  collector: NonSuspendCollector<T, R>
): R = collectI(collector)

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@Suppress("UNINITIALIZED_VARIABLE", "UNCHECKED_CAST")
internal suspend fun <A, T, R> Flow<T>.collectI(
  collector: CollectorI<A, T, R>,
  concurrency: Int = DEFAULT_CONCURRENCY,
): R {
  var accumulator: A
  val started = this.onStart { accumulator = collector.supply() }
  val continued = when {
    Characteristics.CONCURRENT in collector.characteristics -> when {
      Characteristics.UNORDERED in collector.characteristics ->
        started.parMapUnordered(concurrency) { collector.accumulate(accumulator, it) }

      else ->
        started.parMap(concurrency) { collector.accumulate(accumulator, it) }
    }

    else -> started.map { collector.accumulate(accumulator, it) }
  }
  var completed: R
  continued.onCompletion {
    completed = when {
      Characteristics.IDENTITY_FINISH in collector.characteristics -> accumulator as R
      else -> collector.finish(accumulator)
    }
  }.collect { }
  return completed
}

@Suppress("UNCHECKED_CAST")
internal fun <A, T, R> Iterator<T>.collectI(
  collector: NonSuspendCollectorI<A, T, R>
): R {
  val accumulator = collector.supplyNonSuspend()
  forEach { collector.accumulateNonSuspend(accumulator, it) }
  return when {
    Characteristics.IDENTITY_FINISH in collector.characteristics -> accumulator as R
    else -> collector.finishNonSuspend(accumulator)
  }
}
