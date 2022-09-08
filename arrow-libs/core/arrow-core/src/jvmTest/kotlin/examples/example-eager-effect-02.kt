// This file was automatically generated from EagerEffect.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEagerEffect02

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.continuations.eagerEffect
import io.kotest.assertions.fail
import io.kotest.matchers.shouldBe

fun main() {
  eagerEffect<String, Int> {
    val x = Either.Right(1).bind()
    val y = Option(3).bind { "Option was empty" }
    x + y
  }.fold({ fail("Shift can never be the result") }, { it shouldBe 5 })

  eagerEffect<String, Int> {
    val x = Either.Right(1).bind()
    val y: Int = None.bind { "Option was empty" }
    x + y
  }.fold({ it shouldBe "Option was empty" }, { fail("Int can never be the result") })
}
