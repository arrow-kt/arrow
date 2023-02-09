// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither57

import arrow.core.Either
import arrow.core.recover
import io.kotest.matchers.shouldBe

fun test() {
  val error: Either<String, Int> = Either.Left("error")
  val fallback: Either<Nothing, Int> = error.recover { it.length }
  fallback shouldBe Either.Right(5)
}
