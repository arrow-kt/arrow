// This file was automatically generated from Schedule.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleSchedule02

import kotlin.time.seconds
import kotlin.time.milliseconds
import kotlin.time.ExperimentalTime
import arrow.fx.coroutines.*

@ExperimentalTime
fun <A> complexPolicy(): Schedule<A, List<A>> =
  Schedule.exponential<A>(10.milliseconds).whileOutput { it.inNanoseconds < 60.seconds.inNanoseconds }
    .andThen(Schedule.spaced<A>(60.seconds) and Schedule.recurs(100)).jittered()
    .zipRight(Schedule.identity<A>().collect())
