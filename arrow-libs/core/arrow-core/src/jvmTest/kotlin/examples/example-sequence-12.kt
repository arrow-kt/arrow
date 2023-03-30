// This file was automatically generated from Sequence.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleSequence12

import arrow.core.leftIor
import arrow.core.unalignToPair

fun main(args: Array<String>) {
  //sampleStart
  val result = sequenceOf(1, 2, 3).unalignToPair { it.leftIor() }
  //sampleEnd
  println("(${result.first.toList()}, ${result.second.toList()})")
}
