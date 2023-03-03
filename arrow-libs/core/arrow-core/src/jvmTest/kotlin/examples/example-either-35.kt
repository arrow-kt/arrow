// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither35

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import io.kotest.matchers.shouldBe

fun test() {
 Right(12).isRight { it > 10 } shouldBe true
 Right(7).isRight { it > 10 } shouldBe false

 val left: Either<String, Int> = Left("Hello World")
 left.isRight { it > 10 } shouldBe false
}
