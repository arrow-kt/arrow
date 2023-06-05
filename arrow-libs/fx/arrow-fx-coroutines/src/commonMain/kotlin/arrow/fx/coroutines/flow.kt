@file:JvmMultifileClass
@file:JvmName("FlowExtensions")

package arrow.fx.coroutines

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.DEFAULT_CONCURRENCY
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.produceIn
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.zip
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

/**
 * Like [map], but will evaluate [transform] in parallel, emitting the results
 * downstream in **the same order as the input stream**. The number of concurrent effects
 * is limited by [concurrency].
 *
 * If [concurrency] is more than 1, then inner flows are be collected by this operator concurrently.
 * With `concurrency == 1` this operator is identical to [map].
 *
 * Applications of [flowOn], [buffer], and [produceIn] after this operator are fused with its concurrent merging so that only one properly configured channel is used for execution of merging logic.
 *
 * See [parMapUnordered] if there is no requirement to retain the order of the original stream.
 *
 * ```kotlin
 * import kotlinx.coroutines.delay
 * import kotlinx.coroutines.flow.flowOf
 * import kotlinx.coroutines.flow.toList
 * import kotlinx.coroutines.flow.collect
 * import arrow.fx.coroutines.parMap
 *
 * //sampleStart
 * suspend fun main(): Unit {
 *   flowOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
 *     .parMap { a ->
 *       delay(100)
 *       a
 *     }.toList() // [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
 * }
 * //sampleEnd
 * ```
 * <!--- KNIT example-flow-01.kt -->
 * The upstream `source` runs concurrently with downstream `parMap`, and thus the upstream
 * concurrently runs, "prefetching", the next element. i.e.
 *
 *  ```kotlin
 *  import arrow.fx.coroutines.*
 *
 *  suspend fun main(): Unit {
 *  //sampleStart
 *  val source = flowOf(1, 2, 3, 4)
 *  source.parMap(concurrency= 2) {
 *      println("Processing $it")
 *      never<Unit>()
 *    }.collect()
 * //sampleEnd
 * }
 * ```
 * <!--- KNIT example-flow-02.kt -->
 *
 * `1, 2, 3` will be emitted from `source` but only "Processing 1" & "Processing 2" will get printed.
 */
@FlowPreview
@ExperimentalCoroutinesApi
public inline fun <A, B> Flow<A>.parMap(
  concurrency: Int = DEFAULT_CONCURRENCY,
  crossinline transform: suspend CoroutineScope.(a: A) -> B
): Flow<B> =
  channelFlow<Deferred<B>> {
    map { a ->
      // We create deferrable values to keep track of order we receive elements from `map`
      val deferred = CompletableDeferred<B>()
      send(deferred)
      flow<Unit> { // Effect as flow, no emissions
        try {
          val b = transform(a)
          deferred.complete(b)
        } catch (e: Throwable) {
          require(deferred.completeExceptionally(e))
          throw e
        }
      }
    }
      .flattenMerge(concurrency)
      // We don't need a buffer, this flow doesn't emit and we immediately collect
      .buffer(Channel.RENDEZVOUS)
      .launchIn(this)
  }
    .buffer(concurrency)
    .map(Deferred<B>::await)

/**
 * Like [map], but will evaluate effects in parallel, emitting the results downstream.
 * The number of concurrent effects is limited by [concurrency].
 *
 * See [parMap] if retaining the original order of the stream is required.
 *
 * ```kotlin
 * import kotlinx.coroutines.delay
 * import kotlinx.coroutines.flow.flowOf
 * import kotlinx.coroutines.flow.toList
 * import kotlinx.coroutines.flow.collect
 * import arrow.fx.coroutines.parMapUnordered
 *
 * //sampleStart
 * suspend fun main(): Unit {
 *   flowOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
 *     .parMapUnordered { a ->
 *       delay(100)
 *       a
 *     }.toList() // [3, 5, 4, 6, 2, 8, 7, 1, 9, 10]
 * }
 * //sampleEnd
 * ```
 * <!--- KNIT example-flow-03.kt -->
 */
@FlowPreview
public inline fun <A, B> Flow<A>.parMapUnordered(
  concurrency: Int = DEFAULT_CONCURRENCY,
  crossinline transform: suspend (a: A) -> B
): Flow<B> =
  map { o ->
    flow {
      emit(transform(o))
    }
  }.flattenMerge(concurrency)

/** Repeats the Flow forever */
public fun <A> Flow<A>.repeat(): Flow<A> =
  flow {
    while (true) {
      collect {
        emit(it)
      }
    }
  }

/**
 * Flow that emits [A] every [period] while taking into account how much time it takes downstream to consume the emission.
 * If downstream takes longer to process than [period] than it immediately emits another [A].
 *
 * Use `onEach { delay(timeMillis) }` for an alternative that sleeps [period] between every element.
 * This is different in that the time between every element is equal to the specified period,
 * regardless of how much time it takes to process that tick downstream.
 *
 * i.e, for a period of 1 second and a delay(100), the timestamps of the emission would be 1s, 2s, 3s, ... when using [fixedRate].
 * Whereas with `onEach { delay(timeMillis) }` it would run at timestamps 1s, 2.1s, 3.2s, ...
 *
 * @param period period between [Unit] emits of the resulting [Flow].
 */
@ExperimentalTime
public fun <A> Flow<A>.metered(period: Duration): Flow<A> =
  fixedRate(period).zip(this) { _, a -> a }

public fun <A> Flow<A>.metered(period: Long): Flow<A> =
  fixedRate(period).zip(this) { _, a -> a }

@ExperimentalTime
public fun fixedRate(
  period: Duration,
  dampen: Boolean = true,
  timeStampInMillis: () -> Long = { timeInMillis() }
): Flow<Unit> =
  fixedRate(period.inWholeMilliseconds, dampen, timeStampInMillis)

/**
 * Flow that emits [Unit] every [period] while taking into account how much time it takes downstream to consume the emission.
 * If downstream takes longer to process than [period] than it immediately emits another [Unit],
 * if you set [dampen] to false it will send `n = downstreamTime / period` [Unit] elements immediately.
 *
 * Use `onEach { delay(timeMillis) }` for an alternative that sleeps [period] between every element.
 * This is different in that the time between every element is equal to the specified period,
 * regardless of how much time it takes to process that tick downstream.
 *
 * i.e, for a period of 1 second and a delay(100), the timestamps of the emission would be 1s, 2s, 3s, ... when using [fixedRate].
 * Whereas with `onEach { delay(timeMillis) }` it would run at timestamps 1s, 2.1s, 3.2s, ...
 *
 * @param period period between [Unit] emits of the resulting [Flow].
 * @param dampen if you set [dampen] to false it will send `n` times [period] time it took downstream to process the emission.
 * @param timeStampInMillis allows for supplying a different timestamp function, useful to override with `runBlockingTest`
 */
public fun fixedRate(
  period: Long,
  dampen: Boolean = true,
  timeStampInMillis: () -> Long = { timeInMillis() }
): Flow<Unit> =
  if (period == 0L) flowOf(Unit).repeat()
  else flow {
    var lastAwakeAt = timeStampInMillis()

    while (true) {
      val now = timeStampInMillis()
      val next = lastAwakeAt + period

      if (next > now) {
        delay(next - now)
        emit(Unit)
        lastAwakeAt = next
      } else {
        val ticks: Long = (now - lastAwakeAt - 1) / period
        when {
          ticks < 0L -> Unit
          ticks == 0L || dampen -> emit(Unit)
          else -> repeat(ticks.toInt()) { emit(Unit) }
        }
        lastAwakeAt += (period * ticks)
      }
    }
  }

/**
 * Returns a flow containing the results of applying the given [f] function
 * to each value and its index in the original flow.
 */
public inline fun <A, B> Flow<A>.mapIndexed(crossinline f: suspend (index: Int, value: A) -> B): Flow<B> = flow {
  collectIndexed { index, value ->
    emit(f(index, value))
  }
}
