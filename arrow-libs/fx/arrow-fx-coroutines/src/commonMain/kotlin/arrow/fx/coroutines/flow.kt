@file:JvmMultifileClass
@file:JvmName("FlowExtensions")

package arrow.fx.coroutines

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.delayEach
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.zip
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.TimeMark
import kotlin.time.TimeSource

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

public fun <A> Flow<A>.repeat(): Flow<A> =
  flow {
    while (true) {
      collect {
        emit(it)
      }
    }
  }

@ExperimentalTime
public fun <A> Flow<A>.metered(period: Duration): Flow<A> =
  fixedRate(period).zip(this) { _, a -> a }

/**
 * Discrete flow that emits [Unit] every [period].
 *
 * Use `onEach { delay(timeMillis) }` for an alternative that sleeps [period] between every element.
 *
 * This operation differs in that the time between every element is roughly equal to the specified period,
 * regardless of how much time it takes to process that tick downstream.
 *
 * For example, with a 1 second period and a task that takes 100ms, the task would run at timestamps, 1s, 2s, 3s, etc when using `fixedRate.zip(task) { _, a -> a }`.
 * Whereas with delaying each element it would run at timestamps 1s, 2.1s, 3.2s, etc.
 *
 * In the case where task processing takes longer than a single period, 1 or more ticks are immediately emitted to "catch-up".
 * The `dampen` parameter controls whether a single tick is emitted or whether one per missed period is emitted.
 *
 * @param period period between emits of the resulting stream
 * @param dampen true if a single unit should be emitted when multiple periods have passed since last execution, false if a unit for each period should be emitted
 */
@ExperimentalTime
public fun fixedRate(
  period: Duration,
  dampen: Boolean = true
): Flow<Unit> =
  if (period.inWholeNanoseconds == 0L) flowOf(Unit).repeat()
  else flow {
    val period = period.inWholeMilliseconds
    var lastAwakeAt = timeInMillis()

    while (true) {
      val now = timeInMillis()
      val next = lastAwakeAt + period

      if (next > now) {
        delay(next - now)
        emit(Unit)
        lastAwakeAt = next
      } else {
        val ticks = (now - lastAwakeAt - 1) / period
        when {
          ticks < 0 -> Unit
          ticks == 0L || dampen -> emit(Unit)
          else -> repeat(ticks.toInt()) { emit(Unit) }
        }
        lastAwakeAt += (period * ticks)
      }
    }
  }
