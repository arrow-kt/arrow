// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither39

import arrow.core.Either
import io.kotest.matchers.shouldBe

fun main() {
  Either.Right(1).tap(::println) shouldBe Either.Right(1)
  Either.Left(2).tap(::println) shouldBe Either.Left(2)
}
