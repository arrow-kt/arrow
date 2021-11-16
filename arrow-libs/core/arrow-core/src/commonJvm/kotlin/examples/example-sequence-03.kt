// This file was automatically generated from Sequence.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleSequence03

import arrow.core.ifThen

fun main(args: Array<String>) {
  //sampleStart
  val result =
   sequenceOf(1,2,3).ifThen(sequenceOf("empty")) { i ->
     sequenceOf("$i, ${i + 1}")
   }
  //sampleEnd
  println(result.toList())
}
