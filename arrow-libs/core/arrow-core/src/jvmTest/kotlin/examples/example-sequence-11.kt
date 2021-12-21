// This file was automatically generated from Sequence.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleSequence11

import arrow.core.split

fun main(args: Array<String>) {
  //sampleStart
  val result = sequenceOf("A", "B", "C").split()
  //sampleEnd
  result?.let { println("(${it.first.toList()}, ${it.second.toList()})") }
}
