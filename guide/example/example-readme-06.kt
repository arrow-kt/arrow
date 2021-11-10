// This file was automatically generated from README.md by Knit tool. Do not edit.
package example.exampleReadme06

import arrow.*
import arrow.core.*
import arrow.fx.coroutines.*
import kotlinx.coroutines.*
import io.kotest.matchers.collections.*
import io.kotest.assertions.*
import io.kotest.matchers.*
import io.kotest.matchers.types.*
import kotlin.coroutines.cancellation.CancellationException

suspend fun test() {
  val exits = (0..3).map { CompletableDeferred<ExitCase>() }
  cont<String, List<Unit>> {
    (0..4).parTraverse { index ->
      if (index == 4) shift("error")
      else guaranteeCase({ delay(1_000_000) }) { exitCase -> require(exits[index].complete(exitCase)) }
    }
  }.fold({ msg -> msg shouldBe "error" }, { fail("Int can never be the result") })
  exits.awaitAll().forEach { it.shouldBeTypeOf<ExitCase.Cancelled>() }
}
