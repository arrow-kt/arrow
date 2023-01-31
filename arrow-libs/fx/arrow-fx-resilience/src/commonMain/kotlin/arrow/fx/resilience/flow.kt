package arrow.fx.resilience

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retryWhen

/**
 * Retries collection of the given flow when an exception occurs in the upstream flow based on a decision by the [schedule].
 * This operator is *transparent* to exceptions that occur in downstream flow and does not retry on exceptions that are thrown
 * to cancel the flow.
 *
 * @see [Schedule] for how to build a schedule.
 *
 * ```kotlin
 * import kotlinx.coroutines.flow.*
 * import arrow.fx.resilience.*
 *
 * suspend fun main(): Unit {
 *   var counter = 0
 *   val flow = flow {
 *    emit(counter)
 *    if (++counter <= 5) throw RuntimeException("Bang!")
 *   }
 *   //sampleStart
 *  val sum = flow.retry(Schedule.recurs(5))
 *    .reduce(Int::plus)
 *   //sampleEnd
 *   println(sum)
 * }
 * ```
 * <!--- KNIT example-flow-01.kt -->
 *
 * @param schedule - the [Schedule] used for retrying the collection of the flow
 */
@Suppress("UNCHECKED_CAST")
public fun <A, B> Flow<A>.retry(schedule: Schedule<Throwable, B>): Flow<A> = flow {
  (schedule as Schedule.ScheduleImpl<Any?, Throwable, B>)
  var dec: Schedule.Decision<Any?, B>
  var state: Any? = schedule.initialState()

  val retryWhen = retryWhen { cause, _ ->
    dec = schedule.update(cause, state)
    state = dec.state

    if (dec.cont) {
      delay(dec.duration)
      true
    } else {
      false
    }
  }
  retryWhen.collect {
    emit(it)
  }
}
