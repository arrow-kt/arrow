// This file was automatically generated from Control.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleControl10

import arrow.core.continuations.control
import arrow.core.continuations.ensureNotNull
import arrow.core.left
import arrow.core.right
import io.kotest.matchers.shouldBe

suspend fun main() {
  val failure = "failed"
  val int: Int? = null
  control<String, Int> {
    ensureNotNull(int) { failure }
  }.toEither() shouldBe (int?.right() ?: failure.left())
}
