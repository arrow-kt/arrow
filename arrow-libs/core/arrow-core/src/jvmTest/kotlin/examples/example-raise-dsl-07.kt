// This file was automatically generated from Raise.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleRaiseDsl07

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.effect
import io.kotest.matchers.shouldBe

suspend fun test() {
  either<Nothing, Int> {
    effect { raise("failed") }.recover { str -> str.length }
  } shouldBe Either.Right(6)

  either {
    effect { raise("failed") }.recover { str -> raise(-1) }
  } shouldBe Either.Left(-1)
}
