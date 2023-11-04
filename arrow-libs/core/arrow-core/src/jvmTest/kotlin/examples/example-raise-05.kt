// This file was automatically generated from Effect.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleRaise05

import arrow.core.raise.effect
import arrow.core.raise.fold
import arrow.fx.coroutines.ExitCase
import arrow.fx.coroutines.guaranteeCase
import arrow.fx.coroutines.parZip
import io.kotest.assertions.fail
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.awaitCancellation

suspend fun <A> awaitExitCase(exit: CompletableDeferred<ExitCase>): A =
  guaranteeCase({ awaitCancellation() }) { exitCase -> exit.complete(exitCase) }

 suspend fun main() {
   val error = "Error"
   val exit = CompletableDeferred<ExitCase>()
  effect<String, Int> {
    parZip({ awaitExitCase<Int>(exit) }, { raise(error) }) { a: Int, b: Int -> a + b }
  }.fold({ it shouldBe error }, { fail("Int can never be the result") })
  exit.await().shouldBeTypeOf<ExitCase>()
}
