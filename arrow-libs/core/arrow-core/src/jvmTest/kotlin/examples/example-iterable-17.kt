// This file was automatically generated from Iterable.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleIterable17

import arrow.core.*

fun main(args: Array<String>) {
  //sampleStart
  val result =
   listOf(1,2,3).unweave { i -> listOf("$i, ${i + 1}") }
  //sampleEnd
  println(result)
}
