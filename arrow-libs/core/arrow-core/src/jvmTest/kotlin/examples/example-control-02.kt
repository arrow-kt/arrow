// This file was automatically generated from Control.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleControl02

import arrow.core.continuations.control
import io.kotest.matchers.shouldBe

suspend fun main() {
  val shift = control<String, Int> {
    shift("Hello, World!")
  }.fold({ str: String -> str }, { int -> int.toString() })
  shift shouldBe "Hello, World!"

  val res = control<String, Int> {
    1000
  }.fold({ str: String -> str.length }, { int -> int })
  res shouldBe 1000
}
