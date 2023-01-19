// This file was automatically generated from Schedule.kt by Knit tool. Do not edit.
package arrow.fx.resilience.examples.exampleSchedule02

import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import arrow.fx.resilience.*

@ExperimentalTime
fun <A> complexPolicy(): Schedule<A, List<A>> =
  Schedule.exponential<A>(10.milliseconds).whileOutput { it < 60.seconds }
    .andThen(Schedule.spaced<A>(60.seconds) and Schedule.recurs(100)).jittered()
    .zipRight(Schedule.identity<A>().collect())
