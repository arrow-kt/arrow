// This file was automatically generated from EagerEffect.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEagerEffect01

import arrow.core.continuations.eagerEffect
import io.kotest.matchers.shouldBe

fun main() {
  val shift = eagerEffect<String, Int> {
    shift("Hello, World!")
  }.fold({ str: String -> str }, { int -> int.toString() })
  shift shouldBe "Hello, World!"

  val res = eagerEffect<String, Int> {
    1000
  }.fold({ str: String -> str.length }, { int -> int })
  res shouldBe 1000
}
