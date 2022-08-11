// This file was automatically generated from Iterable.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleIterable07

import arrow.core.*

fun main(args: Array<String>) {
  //sampleStart
  val result =
   listOf("A", "B").align(listOf(1, 2, 3)) {
     "$it"
   }
  //sampleEnd
  println(result)
}
