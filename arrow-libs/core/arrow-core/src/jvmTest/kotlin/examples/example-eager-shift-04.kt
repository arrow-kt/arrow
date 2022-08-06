// This file was automatically generated from EagerShift.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEagerShift04

import arrow.core.Either
import arrow.core.continuations.eagerEffect
import arrow.core.continuations.toEither
import io.kotest.matchers.shouldBe

fun main() {
  val either = Either.Right(9)
  eagerEffect<String, Int> {
    val x: Int = either.bind()
    x
  }.toEither() shouldBe either
}
