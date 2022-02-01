// This file was automatically generated from EffectContext.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEffect07

import arrow.core.Either
import arrow.core.continuations.effect
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
