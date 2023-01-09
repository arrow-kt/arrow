// This file was automatically generated from Schedule.kt by Knit tool. Do not edit.
package arrow.fx.resilience.examples.exampleSchedule08

import kotlin.time.milliseconds
import kotlin.time.ExperimentalTime
import arrow.fx.resilience.*

@ExperimentalTime
val exponential = Schedule.exponential<Unit>(250.milliseconds)
