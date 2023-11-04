// This file was automatically generated from Sequence.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleSequence12

import arrow.core.leftIor
import arrow.core.unalign

fun main() {
  //sampleStart
  val result = sequenceOf(1, 2, 3).unalign { it.leftIor() }
  //sampleEnd
  println("(${result.first.toList()}, ${result.second.toList()})")
}
