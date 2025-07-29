// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither23

import arrow.core.Either
import io.kotest.matchers.shouldBe
import io.kotest.assertions.AssertionErrorBuilder.Companion.fail

fun test() {
  Either.Right(1)
    .fold({ fail("Cannot be left") }, { it + 1 }) shouldBe 2

  Either.Left(RuntimeException("Boom!"))
    .fold({ -1 }, { fail("Cannot be right") }) shouldBe -1
}
