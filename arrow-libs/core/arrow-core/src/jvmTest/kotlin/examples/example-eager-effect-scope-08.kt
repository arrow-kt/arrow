// This file was automatically generated from EagerEffectScope.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEagerEffectScope08

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Validated
import arrow.core.continuations.eagerEffect
import io.kotest.assertions.fail
import io.kotest.matchers.shouldBe

fun main() {
  eagerEffect<String, Int> {
    val x = Either.Right(1).bind()
    val y = Validated.Valid(2).bind()
    val z =
     attempt { None.bind { "Option was empty" } } catch { 0 }
    x + y + z
  }.fold({ fail("Shift can never be the result") }, { it shouldBe 3 })
}
