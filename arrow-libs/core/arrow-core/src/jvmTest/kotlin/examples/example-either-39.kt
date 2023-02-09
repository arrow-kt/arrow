// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither39

import arrow.core.Either
import io.kotest.matchers.shouldBe

fun test() {
 Either.Right(12).mapLeft { _: Nothing -> "flower" } shouldBe Either.Right(12)
 Either.Left(12).mapLeft { _: Int -> "flower" }  shouldBe Either.Left("flower")
}
