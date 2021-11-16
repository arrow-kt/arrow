// This file was automatically generated from Iterable.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleIterable12

import arrow.core.*

fun main(args: Array<String>) {
  //sampleStart
  val result =
     listOf(1, 2, 3).unalign {
       it.leftIor()
     }
  //sampleEnd
  println(result)
}
