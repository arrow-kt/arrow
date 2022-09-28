// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither54

import arrow.core.Either
import arrow.core.recover
import io.kotest.matchers.shouldBe

fun main() {
  val error: Either<String, Int> = Either.Left("error")
  val fallback: Either<Nothing, Int> = error.recover { it.length }
  fallback shouldBe Either.Right(5)
}
