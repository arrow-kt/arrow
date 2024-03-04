// This file was automatically generated from Sequence.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleSequence13

import arrow.core.unweave

fun main() {
  //sampleStart
  val result = sequenceOf(1,2,3).unweave { i -> sequenceOf("$i, ${i + 1}") }
  //sampleEnd
  println(result.toList())
}
