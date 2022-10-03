// This file was automatically generated from map.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleMap01

import arrow.core.*

fun main(args: Array<String>) {
  //sampleStart
  val result =
   mapOf(1 to "A", 2 to "B").zip(mapOf(1 to "1", 2 to "2", 3 to "3"))
  //sampleEnd
  println(result)
}
