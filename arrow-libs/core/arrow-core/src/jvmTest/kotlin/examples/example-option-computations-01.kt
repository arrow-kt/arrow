// This file was automatically generated from option.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleOptionComputations01

import arrow.core.computations.option

suspend fun main() {
  option<Int> {
    ensure(true)
    println("ensure(true) passes")
    ensure(false)
    1
  }
  .let(::println)
}
// println: "ensure(true) passes"
// res: None
