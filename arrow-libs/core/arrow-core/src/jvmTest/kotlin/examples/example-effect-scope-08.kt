// This file was automatically generated from Shift.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEffectScope08

import arrow.core.Either
import arrow.core.continuations.effect
import arrow.core.continuations.toEither
import io.kotest.matchers.shouldBe

suspend fun main() {
  val condition = true
  val failure = "failed"
  val int = 4
  effect<String, Int> {
    ensure(condition) { failure }
    int
  }.toEither() shouldBe if(condition) Either.Right(int) else Either.Left(failure)
}
