// This file was automatically generated from Race2.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleRace201

import arrow.core.Either
import arrow.fx.coroutines.*
import kotlinx.coroutines.awaitCancellation

suspend fun main(): Unit {
  suspend fun loser(): Int =
    guaranteeCase({ awaitCancellation() }) { exitCase ->
      println("I can never win the race. Finished with $exitCase.")
    }

  val winner = raceN({ loser() }, { 5 })

  val res = when(winner) {
    is Either.Left -> "Never always loses race"
    is Either.Right -> "Race was won with ${winner.value}"
  }
  //sampleEnd
  println(res)
}
