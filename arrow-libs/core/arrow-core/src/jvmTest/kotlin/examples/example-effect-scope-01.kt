// This file was automatically generated from EffectScope.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEffectScope01

import arrow.core.continuations.effect
import arrow.core.continuations.fold
import io.kotest.assertions.fail
import io.kotest.matchers.shouldBe

suspend fun main() {
  effect<String, Int> {
    shift("SHIFT ME")
  }.fold({ it shouldBe "SHIFT ME" }, { fail("Computation never finishes") })
}
