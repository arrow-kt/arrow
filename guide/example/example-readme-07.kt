// This file was automatically generated from README.md by Knit tool. Do not edit.
package example.exampleReadme07

import arrow.*
import arrow.core.*
import arrow.fx.coroutines.*
import kotlinx.coroutines.*
import io.kotest.matchers.collections.*
import io.kotest.assertions.*
import io.kotest.matchers.*
import io.kotest.matchers.types.*
import kotlin.coroutines.cancellation.CancellationException
import io.kotest.property.*
import io.kotest.property.arbitrary.*

suspend fun test() {
  val exit = CompletableDeferred<ExitCase>()
  cont<String, Int> {
    raceN({
      guaranteeCase({ delay(1_000_000) }) { exitCase -> require(exit.complete(exitCase))  }
      5
    }) { shift<Int>("error") }
      .merge() // Flatten Either<Int, Int> result from race into Int
  }.fold({ msg -> msg shouldBe "error" }, { fail("Int can never be the result") })
  exit.await().shouldBeTypeOf<ExitCase.Cancelled>()
}
