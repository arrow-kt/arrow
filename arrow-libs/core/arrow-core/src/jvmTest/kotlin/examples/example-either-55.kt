// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither55

import arrow.core.Either
import arrow.core.recover
import io.kotest.matchers.shouldBe

fun main() {
  val error: Either<String, Int> = Either.Left("error")
  val resolved: Either<Nothing, Int> = error.recover { 1 }
  val listOfErrors: Either<List<Char>, Int> = error.recover { shift(it.toList()) }

  resolved shouldBe Either.Right(1)
  listOfErrors shouldBe Either.Left(listOf('e', 'r', 'r', 'o', 'r'))
}
