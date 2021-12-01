// This file was automatically generated from either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEitherComputations01

import arrow.core.computations.either

suspend fun main() {
  either<String, Int> {
    ensure(true) { "" }
    println("ensure(true) passes")
    ensure(false) { "failed" }
    1
  }
  .let(::println)
}
// println: "ensure(true) passes"
// res: Either.Left("failed")
