// This file was automatically generated from EagerEffectScope.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEagerEffectScope04

import arrow.core.Validated
import arrow.core.continuations.eagerEffect
import io.kotest.matchers.shouldBe

fun main() {
  val validated = Validated.Valid(40)
  eagerEffect<String, Int> {
    val x: Int = validated.bind()
    x
  }.toValidated() shouldBe validated
}
