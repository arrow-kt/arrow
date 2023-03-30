// This file was automatically generated from Sequence.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleSequence11

import arrow.core.bothIor
import arrow.core.leftIor
import arrow.core.unalignToPair

fun main(args: Array<String>) {
  //sampleStart
  val result = sequenceOf(("A" to 1).bothIor(), ("B" to 2).bothIor(), "C".leftIor()).unalignToPair()
  //sampleEnd
  println("(${result.first}, ${result.second})")
}
