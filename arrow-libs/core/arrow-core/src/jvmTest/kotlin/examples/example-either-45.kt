// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither45

import arrow.core.Either.Right
import arrow.core.Either.Left
import arrow.core.getOrElse
import io.kotest.matchers.shouldBe

fun main() {
  Right(12).getOrHandle { it + 5 } shouldBe 12
  Left(12).getOrHandle { it + 5 } shouldBe 17
}
