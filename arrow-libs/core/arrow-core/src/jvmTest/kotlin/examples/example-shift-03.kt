// This file was automatically generated from Shift.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleShift03

import arrow.core.Either
import arrow.core.continuations.EagerEffect
import arrow.core.continuations.eagerEffect
import arrow.core.continuations.effect
import arrow.core.continuations.toEither
import arrow.core.identity
import io.kotest.matchers.shouldBe

suspend fun main() {
  val eager = eagerEffect<String, Int> { shift("error") }
  effect<String, Int> {
    val x: Int = eager()
    x
  }.toEither() shouldBe Either.Left("error")
}
