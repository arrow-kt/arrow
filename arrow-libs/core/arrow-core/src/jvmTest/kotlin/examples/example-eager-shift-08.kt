// This file was automatically generated from EagerShift.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEagerShift08

import arrow.core.Either
import arrow.core.continuations.eagerEffect
import arrow.core.continuations.toEither
import io.kotest.matchers.shouldBe

fun main() {
  val condition = true
  val failure = "failed"
  val int = 4
  eagerEffect<String, Int> {
    ensure(condition) { failure }
    int
  }.toEither() shouldBe if(condition) Either.Right(int) else Either.Left(failure)
}
