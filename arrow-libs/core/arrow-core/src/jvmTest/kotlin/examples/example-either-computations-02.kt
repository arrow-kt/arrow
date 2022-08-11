// This file was automatically generated from either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEitherComputations02

import arrow.core.computations.either
import arrow.core.computations.ensureNotNull

suspend fun main() {
  either<String, Int> {
    val x: Int? = 1
    ensureNotNull(x) { "passes" }
    println(x)
    ensureNotNull(null) { "failed" }
  }
  .let(::println)
}
// println: "1"
// res: Either.Left("failed")
