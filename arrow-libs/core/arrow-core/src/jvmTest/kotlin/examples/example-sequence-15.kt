// This file was automatically generated from Sequence.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleSequence15

import arrow.core.unzipToPair

fun main(args: Array<String>) {
  //sampleStart
  val result =
   sequenceOf("A:1", "B:2", "C:3").unzipToPair { e ->
     e.split(":").let {
       it.first() to it.last()
     }
   }
  //sampleEnd
  println("(${result.first}, ${result.second})")
}
