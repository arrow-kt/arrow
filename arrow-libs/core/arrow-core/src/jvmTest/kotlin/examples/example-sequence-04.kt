// This file was automatically generated from Sequence.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleSequence04

import arrow.core.interleave

fun main(args: Array<String>) {
  //sampleStart
  val tags = generateSequence { "#" }.take(10)
  val result =
   tags.interleave(sequenceOf("A", "B", "C"))
  //sampleEnd
  println(result.toList())
}
