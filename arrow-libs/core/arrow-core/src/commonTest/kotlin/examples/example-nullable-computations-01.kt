// This file was automatically generated from nullable.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleNullableComputations01

import arrow.core.computations.nullable

suspend fun main() {
  nullable<Int> {
    ensure(true)
    println("ensure(true) passes")
    ensure(false)
    1
  }
  .let(::println)
}
// println: "ensure(true) passes"
// res: null
