// This file was automatically generated from EagerShift.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEagerShift05

import arrow.core.Validated
import arrow.core.continuations.eagerEffect
import arrow.core.continuations.toValidated
import io.kotest.matchers.shouldBe

fun main() {
  val validated = Validated.Valid(40)
  eagerEffect<String, Int> {
    val x: Int = validated.bind()
    x
  }.toValidated() shouldBe validated
}
