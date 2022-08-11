// This file was automatically generated from Sequence.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleSequence01

import arrow.core.align

fun main(args: Array<String>) {
  //sampleStart
  val result =
   sequenceOf("A", "B").align(sequenceOf(1, 2, 3)) {
     "$it"
   }
  //sampleEnd
  println(result.toList())
}
