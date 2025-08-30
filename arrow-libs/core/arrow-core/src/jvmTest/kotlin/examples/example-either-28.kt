// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither28

import arrow.core.Either
import io.kotest.matchers.shouldBe

fun test() {
  Either.Right(1).onRightBind { print(it) } shouldBe Either.Right(1)

  val x: Either<String, Int> = Either.Right(2)
  x.onRightBind { raise("hello") } shouldBe Either.Left("hello")
}
