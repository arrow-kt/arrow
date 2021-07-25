@file:JvmMultifileClass
@file:JvmName("FlowExtensions")

package arrow.fx.coroutines

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.DEFAULT_CONCURRENCY
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retryWhen
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

/**
 * Retries collection of the given flow when an exception occurs in the upstream flow based on a decision by the [schedule].
 * This operator is *transparent* to exceptions that occur in downstream flow and does not retry on exceptions that are thrown
 * to cancel the flow.
 *
 * @see [Schedule] for how to build a schedule.
 *
 * ```kotlin:ank:playground
 * import kotlinx.coroutines.flow.*
 * import arrow.fx.coroutines.*
 * suspend fun main(): Unit {
 *   var counter = 0
 *   val flow = flow {
 *    emit(a)
 *    if (++counter <= 5) throw RuntimeException("Bang!")
 *   }
 *   //sampleStart
 *  val sum = flow.retry(Schedule.recurs(5))
 *    .reduce { acc, int -> acc + int }
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 *
 * @param schedule - the [Schedule] used for retrying the collection of the flow
 */
public fun <A, B> Flow<A>.retry(schedule: Schedule<Throwable, B>): Flow<A> = flow {
  (schedule as Schedule.ScheduleImpl<Any?, Throwable, B>)
  var dec: Schedule.Decision<Any?, B>
  var state: Any? = schedule.initialState()

  val retryWhen = retryWhen { cause, _ ->
    dec = schedule.update(cause, state)
    state = dec.state

    if (dec.cont) {
      delay((dec.delayInNanos / 1_000_000).toLong())
      true
    } else {
      false
    }
  }
  retryWhen.collect {
    emit(it)
  }
}

/**
 * Like [Flow.map], but will evaluate effects in parallel, emitting the results
 * downstream in the same order as the input stream. The number of concurrent effects
 * is limited by [concurrency].
 *
 * See [parMapUnordered] if there is no requirement to retain the order of the original stream.
 *
 * ```kotlin:ank:playground
 * import kotlinx.coroutines.delay
 * import kotlinx.coroutines.flow.flowOf
 * import kotlinx.coroutines.flow.toList
 * import arrow.fx.coroutines.parMap
 *
 * //sampleStart
 * suspend fun main(): Unit =
 * flowOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
 *   .parMap { a ->
 *     delay(100)
 *     a
 *   }.toList() // [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
 * //sampleEnd
 * ```
 */
@FlowPreview
@ExperimentalCoroutinesApi
public inline fun <A, B> Flow<A>.parMap(
  context: CoroutineContext = Dispatchers.Default,
  concurrency: Int = DEFAULT_CONCURRENCY,
  crossinline transform: suspend CoroutineScope.(a: A) -> B
): Flow<B> =
  channelFlow<Deferred<B>> {
    map { a ->
      val deferred = CompletableDeferred<B>()
      send(deferred)
      flow<Unit> {
        try {
          val b = transform(a)
          deferred.complete(b)
        } catch (e: Throwable) {
          require(deferred.completeExceptionally(e))
          throw e
        }
      }.flowOn(context)
    }
      .flattenMerge(concurrency)
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
 * ```kotlin:ank:playground
 * import kotlinx.coroutines.delay
 * import kotlinx.coroutines.flow.flowOf
 * import kotlinx.coroutines.flow.toList
 * import arrow.fx.coroutines.parMapUnordered
 *
 * //sampleStart
 * suspend fun main(): Unit =
 * flowOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
 *   .parMapUnordered { a ->
 *     delay(100)
 *     a
 *   }.toList() // [3, 5, 4, 6, 2, 8, 7, 1, 9, 10]
 * //sampleEnd
 * ```
 */
@FlowPreview
public inline fun <A, B> Flow<A>.parMapUnordered(
  ctx: CoroutineContext = Dispatchers.Default,
  concurrency: Int = DEFAULT_CONCURRENCY,
  crossinline transform: suspend (a: A) -> B
): Flow<B> =
  map { o ->
    flow {
      emit(transform(o))
    }.flowOn(ctx)
  }.flattenMerge(concurrency)
