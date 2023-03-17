// This file was automatically generated from Raise.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleRaiseDsl05

import arrow.core.Either
import arrow.core.right
import arrow.core.raise.either
import arrow.core.raise.recover
import io.kotest.matchers.shouldBe

fun test() {
  val one: Either<Nothing, Int> = 1.right()
  val left: Either<String, Int> = Either.Left("failed")

  either {
    val x = one.bind()
    val y = recover({ left.bind() }) { failure : String -> 1 }
    x + y
  } shouldBe Either.Right(2)
}
