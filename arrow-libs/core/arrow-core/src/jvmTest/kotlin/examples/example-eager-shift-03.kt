// This file was automatically generated from EagerShift.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEagerShift03

import arrow.core.Either
import arrow.core.continuations.EagerEffect
import arrow.core.continuations.eagerEffect
import arrow.core.continuations.toEither
import io.kotest.matchers.shouldBe

fun main() {
  eagerEffect<String, Int> {
    val x: Int = shift<Int>("error")
    x + 1
  }.toEither() shouldBe Either.Left("error")
}
