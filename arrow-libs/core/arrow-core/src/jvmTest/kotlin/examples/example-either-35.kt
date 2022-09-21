// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither35

import arrow.core.Either
import io.kotest.matchers.shouldBe

fun main() {
  Either.Left("left").swap() shouldBe Either.Right("left")
  Either.Right("right").swap() shouldBe Either.Left("right")
}
