// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither32

import arrow.core.Either
import arrow.core.raise.ensure
import io.kotest.matchers.shouldBe

fun test() {
  val one: Either<String, Int> = Either.Right(1)
  one.validate { ensure(it > 0) { "negative" } } shouldBe one

  val zero: Either<String, Int> = Either.Right(0)
  zero.validate { ensure(it > 0) { "negative" } } shouldBe Either.Left("negative")
}
