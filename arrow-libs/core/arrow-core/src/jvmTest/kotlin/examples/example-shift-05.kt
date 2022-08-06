// This file was automatically generated from Shift.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleShift05

import arrow.core.Validated
import arrow.core.continuations.effect
import arrow.core.continuations.toValidated
import io.kotest.matchers.shouldBe

suspend fun main() {
  val validated = Validated.Valid(40)
  effect<String, Int> {
    val x: Int = validated.bind()
    x
  }.toValidated() shouldBe validated
}
