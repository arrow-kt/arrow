// This file was automatically generated from Effect.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEffectGuide07

import arrow.core.continuations.effect
import arrow.core.merge
import arrow.fx.coroutines.ExitCase
import arrow.fx.coroutines.guaranteeCase
import arrow.fx.coroutines.raceN
import io.kotest.assertions.fail
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.awaitCancellation

suspend fun <A> awaitExitCase(exit: CompletableDeferred<ExitCase>): A =
  guaranteeCase(::awaitCancellation) { exitCase -> exit.complete(exitCase) }

suspend fun <A> CompletableDeferred<A>.getOrNull(): A? =
  if (isCompleted) await() else null

suspend fun main() {
  val error = "Error"
  val exit = CompletableDeferred<ExitCase>()
  effect<String, Int> {
    raceN({ awaitExitCase<Int>(exit) }) { shift<Int>(error) }
      .merge() // Flatten Either<Int, Int> result from race into Int
  }.fold({ msg -> msg shouldBe error }, { fail("Int can never be the result") })
  // It's possible not all parallel task got launched, and in those cases awaitCancellation never ran
  exit.getOrNull()?.shouldBeTypeOf<ExitCase.Cancelled>()
}
