// This file was automatically generated from Effect.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleRaise06

import arrow.core.raise.effect
import arrow.core.raise.fold
import arrow.fx.coroutines.ExitCase
import arrow.fx.coroutines.guaranteeCase
import arrow.fx.coroutines.parMap
import io.kotest.assertions.AssertionErrorBuilder.Companion.fail
import io.kotest.matchers.shouldBe
import arrow.core.shouldBeTypeOf
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.awaitCancellation

suspend fun <A> awaitExitCase(exit: CompletableDeferred<ExitCase>): A =
 guaranteeCase(::awaitCancellation) { exitCase -> exit.complete(exitCase) }

suspend fun <A> CompletableDeferred<A>.getOrNull(): A? =
 if (isCompleted) await() else null

suspend fun main() {
  val error = "Error"
  val exits = (0..3).map { CompletableDeferred<ExitCase>() }
  effect<String, List<Unit>> {
    (0..4).parMap { index ->
      if (index == 4) raise(error)
      else awaitExitCase(exits[index])
    }
  }.fold({ msg -> msg shouldBe error }, { fail("Int can never be the result") })
  // It's possible not all parallel task got launched, and in those cases awaitCancellation never ran
  exits.forEach { exit -> exit.getOrNull()?.shouldBeTypeOf<ExitCase.Cancelled>() }
}
