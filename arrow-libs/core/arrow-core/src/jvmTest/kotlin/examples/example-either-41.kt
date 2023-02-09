// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither41

import arrow.core.Either
import io.kotest.matchers.shouldBe

fun test() {
  Either.Left(2).onLeft(::println) shouldBe Either.Left(2)
}
