// This file was automatically generated from Control.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEffect03

import arrow.core.continuations.effect
import io.kotest.assertions.fail
import io.kotest.matchers.shouldBe

suspend fun main() {
  effect<String, Int> {
    shift("SHIFT ME")
  }.fold({ it shouldBe "SHIFT ME" }, { fail("Computation never finishes") })
}
