// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither30

import arrow.core.Either
import io.kotest.matchers.shouldBe

fun test() {
  Either.Left(2).onLeftBind { println(it) } shouldBe Either.Left(2)

  val x: Either<String, Int> = Either.Left("hello")
  x.onLeftBind { raise("bye") } shouldBe Either.Left("bye")
}
