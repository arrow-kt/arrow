// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither38

import arrow.core.Either
import io.kotest.matchers.shouldBe

fun test() {
  Either.Right(1).onRight(::println) shouldBe Either.Right(1)
}
