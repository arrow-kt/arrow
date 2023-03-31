// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither21

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import io.kotest.matchers.shouldBe

fun test() {
 Left(12).isLeft { it > 10 } shouldBe true
 Left(7).isLeft { it > 10 } shouldBe false

 val right: Either<Int, String> = Right("Hello World")
 right.isLeft { it > 10 } shouldBe false
}
