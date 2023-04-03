// This file was automatically generated from Sequence.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleSequence16

import arrow.core.unzip

fun main(args: Array<String>) {
  //sampleStart
  val result =
   sequenceOf("A:1", "B:2", "C:3").unzip { e ->
     e.split(":").let {
       it.first() to it.last()
     }
   }
  //sampleEnd
  println("(${result.first}, ${result.second})")
}
