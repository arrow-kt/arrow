// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither45

import arrow.core.Either
import arrow.core.Some
import arrow.core.None
import io.kotest.matchers.shouldBe

fun test() {
  Either.Right(12).getOrNone() shouldBe Some(12)
  Either.Left(12).getOrNone() shouldBe None
}
