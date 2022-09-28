// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither45

import arrow.core.Either.Left
import arrow.core.getOrElse
import io.kotest.matchers.shouldBe

fun main() {
  Left(12).getOrElse { it + 5 } shouldBe 17
}
