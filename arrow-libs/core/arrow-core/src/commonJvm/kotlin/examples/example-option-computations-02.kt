// This file was automatically generated from option.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleOptionComputations02

import arrow.core.computations.option

suspend fun main() {
  option<Int> {
    val x: Int? = 1
    ensureNotNull(x)
    println(x)
    ensureNotNull(null)
  }
  .let(::println)
}
// println: "1"
// res: None
