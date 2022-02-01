// This file was automatically generated from Control.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleControl03

import arrow.core.continuations.control
import io.kotest.assertions.fail
import io.kotest.matchers.shouldBe

suspend fun main() {
  control<String, Int> {
    shift("SHIFT ME")
  }.fold({ it shouldBe "SHIFT ME" }, { fail("Computation never finishes") })
}
