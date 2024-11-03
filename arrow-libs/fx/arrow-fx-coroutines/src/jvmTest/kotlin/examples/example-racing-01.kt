// This file was automatically generated from Racing.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleRacing01

import arrow.fx.coroutines.race
import arrow.fx.coroutines.raceOrFail
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.selects.select

suspend fun winner(): String = coroutineScope {
  select {
    race { delay(1000); "Winner" }
    race { throw RuntimeException("Loser") }
  }
} // Winner (logged RuntimeException)

suspend fun winner2(): String = coroutineScope {
  select {
    race { delay(1000); "Winner" }
    raceOrFail { throw RuntimeException("Loser") }
  }
} // RuntimeException

suspend fun never(): Nothing = select { }
