// This file was automatically generated from README.md by Knit tool. Do not edit.
package example.exampleReadme07

import arrow.cont
import arrow.core.merge
import arrow.fx.coroutines.raceN
import kotlinx.coroutines.delay

suspend fun race() = cont<String, Int> {
  raceN({
   delay(1_000_000) // Cancelled by shift
   5
  }) { shift<Int>("error") }
   .merge() // Flatten Either<Int, Int> result from race into Int
}.fold(::println, ::println) // "error"
