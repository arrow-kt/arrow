// This file was automatically generated from Control.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleControl09

import arrow.core.Either
import arrow.core.continuations.control
import io.kotest.matchers.shouldBe

suspend fun main() {
  val condition = true
  val failure = "failed"
  val int = 4
  control<String, Int> {
    ensure(condition) { failure }
    int
  }.toEither() shouldBe if(condition) Either.Right(int) else Either.Left(failure)
}
