// This file was automatically generated from Control.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleControl06

import arrow.core.Validated
import arrow.core.continuations.control
import io.kotest.matchers.shouldBe

suspend fun main() {
  val validated = Validated.Valid(40)
  control<String, Int> {
    val x: Int = validated.bind()
    x
  }.toValidated() shouldBe validated
}
