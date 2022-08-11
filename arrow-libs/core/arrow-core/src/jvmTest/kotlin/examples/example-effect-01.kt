// This file was automatically generated from Effect.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEffect01

import arrow.core.continuations.effect
import io.kotest.matchers.shouldBe

suspend fun main() {
  val shift = effect<String, Int> {
    shift("Hello, World!")
  }.fold({ str: String -> str }, { int -> int.toString() })
  shift shouldBe "Hello, World!"

  val res = effect<String, Int> {
    1000
  }.fold({ str: String -> str.length }, { int -> int })
  res shouldBe 1000
}
