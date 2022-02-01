// This file was automatically generated from EffectContext.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEffectContext03

import arrow.core.Either
import arrow.core.continuations.effect
import io.kotest.matchers.shouldBe

suspend fun main() {
  val either = Either.Right(9)
  effect<String, Int> {
    val x: Int = either.bind()
    x
  }.toEither() shouldBe either
}
