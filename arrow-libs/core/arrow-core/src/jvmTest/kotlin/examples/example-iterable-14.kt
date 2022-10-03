// This file was automatically generated from Iterable.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleIterable14

import arrow.core.*

fun main(args: Array<String>) {
  //sampleStart
  val tags = List(10) { "#" }
  val result =
   tags.interleave(listOf("A", "B", "C"))
  //sampleEnd
  println(result)
}
