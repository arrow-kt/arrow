// This file was automatically generated from Raise.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleRaiseDsl11

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.withError
import io.kotest.matchers.shouldBe

fun test() {
  either<Int, String> {
    withError(String::length) {
      raise("failed")
    }
  } shouldBe Either.Left(6)
}
