// This file was automatically generated from EagerShift.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEagerShift02

import arrow.core.continuations.eagerEffect
import arrow.core.continuations.fold
import io.kotest.assertions.fail
import io.kotest.matchers.shouldBe

fun main() {
  eagerEffect<String, Int> {
    shift("SHIFT ME")
  }.fold({ it shouldBe "SHIFT ME" }, { fail("Computation never finishes") })
}
