// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither41

import arrow.core.Either
import io.kotest.matchers.shouldBe

fun main() {
  Either.Right(12).getOrNull() shouldBe 12
  Either.Left(12).getOrNull() shouldBe null
}
