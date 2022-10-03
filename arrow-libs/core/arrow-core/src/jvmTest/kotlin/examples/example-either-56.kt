// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither56

import arrow.core.Either
import arrow.core.recover
import io.kotest.matchers.shouldBe

fun test() {
  val error: Either<String, Int> = Either.Left("error")
  val listOfErrors: Either<List<Char>, Int> = error.recover { shift(it.toList()) }
  listOfErrors shouldBe Either.Left(listOf('e', 'r', 'r', 'o', 'r'))
}
