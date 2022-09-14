// This file was automatically generated from Effect.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEffect02

import arrow.core.Either
import arrow.core.Ior
import arrow.core.None
import arrow.core.Option
import arrow.core.continuations.effect
import io.kotest.assertions.fail
import io.kotest.matchers.shouldBe

suspend fun main() {
  effect<String, Int> {
    val x = Either.Right(1).bind()
    val y = Either.Right(2).bind()
    val z = Option(3).bind { "Option was empty" }
    x + y + z
  }.fold({ fail("Shift can never be the result") }, { it shouldBe 6 })

  effect<String, Int> {
    val x = Either.Right(1).bind()
    val y = Either.Right(2).bind()
    val z: Int = None.bind { "Option was empty" }
    x + y + z
  }.fold({ it shouldBe "Option was empty" }, { fail("Int can never be the result") })
}
