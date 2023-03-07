// This file was automatically generated from Iterable.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleIterable11

import arrow.core.*

fun main(args: Array<String>) {
  //sampleStart
  val result =
     listOf("A" to 1, "B" to 2).unzip()
  //sampleEnd
  println(result)
}
