// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither36

import arrow.core.Either
import io.kotest.matchers.shouldBe

fun test() {
  Either.Right(12).map { _: Int ->"flower" } shouldBe Either.Right("flower")
  Either.Left(12).map { _: Nothing -> "flower" } shouldBe Either.Left(12)
}
