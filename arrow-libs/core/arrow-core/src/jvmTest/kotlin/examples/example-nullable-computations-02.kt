// This file was automatically generated from nullable.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleNullableComputations02

import arrow.core.computations.nullable
import arrow.core.computations.ensureNotNull

suspend fun main() {
  nullable<Int> {
    val x: Int? = 1
    ensureNotNull(x)
    println(x)
    ensureNotNull(null)
  }
  .let(::println)
}
// println: "1"
// res: null
