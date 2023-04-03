// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither31

import arrow.core.Either
import io.kotest.matchers.shouldBe

fun test() {
  Either.Right(12).leftOrNull() shouldBe null
  Either.Left(12).leftOrNull() shouldBe 12
}
