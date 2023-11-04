// This file was automatically generated from Sequence.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleSequence11

import arrow.core.bothIor
import arrow.core.leftIor
import arrow.core.unalign

fun main() {
  //sampleStart
  val result = sequenceOf(("A" to 1).bothIor(), ("B" to 2).bothIor(), "C".leftIor()).unalign()
  //sampleEnd
  println("(${result.first}, ${result.second})")
}
