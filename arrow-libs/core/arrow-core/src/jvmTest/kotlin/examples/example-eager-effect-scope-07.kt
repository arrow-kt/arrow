// This file was automatically generated from EagerEffectScope.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEagerEffectScope07

import arrow.core.continuations.eagerEffect
import arrow.core.continuations.ensureNotNull
import arrow.core.left
import arrow.core.right
import io.kotest.matchers.shouldBe

fun main() {
  val failure = "failed"
  val int: Int? = null
  eagerEffect<String, Int> {
    ensureNotNull(int) { failure }
  }.toEither() shouldBe (int?.right() ?: failure.left())
}
