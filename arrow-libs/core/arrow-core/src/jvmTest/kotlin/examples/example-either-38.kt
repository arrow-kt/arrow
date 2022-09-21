// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither38

import arrow.core.Either
import io.kotest.matchers.shouldBe

fun main() {
  Either.Right(1).tapLeft(::println) shouldBe Either.Right(1)
  Either.Left(2).tapLeft(::println) shouldBe Either.Left(2)
}
