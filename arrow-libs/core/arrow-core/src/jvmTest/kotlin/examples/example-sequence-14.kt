// This file was automatically generated from Sequence.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleSequence14

import arrow.core.unzip

fun main(args: Array<String>) {
  //sampleStart
  val result = sequenceOf("A" to 1, "B" to 2).unzip()
  //sampleEnd
  println("(${result.first}, ${result.second})")
}
