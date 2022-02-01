// This file was automatically generated from Control.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEffect06

import arrow.core.Validated
import arrow.core.continuations.effect
import io.kotest.matchers.shouldBe

suspend fun main() {
  val validated = Validated.Valid(40)
  effect<String, Int> {
    val x: Int = validated.bind()
    x
  }.toValidated() shouldBe validated
}
