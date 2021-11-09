// This file was automatically generated from README.md by Knit tool. Do not edit.
package example.exampleReadme07

import arrow.cont
import arrow.core.merge
import arrow.fx.coroutines.onCancel
import arrow.fx.coroutines.raceN
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
  cont<String, Int> {
    raceN({
      onCancel({ delay(1_000_000) }) { println("I lost the race...") }
      5
    }) { shift<Int>("error") }
      .merge() // Flatten Either<Int, Int> result from race into Int
  }.fold(::println, ::println)
}
