// This file was automatically generated from Shift.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEffectScope10

import arrow.core.continuations.effect
import arrow.core.continuations.toEither
import arrow.core.continuations.ensureNotNull
import arrow.core.left
import arrow.core.right
import io.kotest.matchers.shouldBe

suspend fun main() {
  val failure = "failed"
  val int: Int? = null
  effect<String, Int> {
    ensureNotNull(int) { failure }
  }.toEither() shouldBe (int?.right() ?: failure.left())
}
