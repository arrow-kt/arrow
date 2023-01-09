// This file was automatically generated from Schedule.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleSchedule08

import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime
import arrow.fx.coroutines.*

@ExperimentalTime
val exponential = Schedule.exponential<Unit>(250.milliseconds)
