// This file was automatically generated from Raise.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleRaiseDsl03

import arrow.core.Either
import arrow.core.raise.Raise
import arrow.core.raise.either
import arrow.core.raise.recover
import arrow.core.recover
import io.kotest.matchers.shouldBe

fun Raise<String>.failure(): Int = raise("failed")

fun Raise<Nothing>.recovered(): Int = recover({ failure() }) { _: String -> 1 }

fun test() {
  val either = either { failure() }
    .recover { _: String -> recovered() }

  either shouldBe Either.Right(1)
}
