// This file was automatically generated from map.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleMap06

import arrow.core.*

fun main(args: Array<String>) {
  //sampleStart
  val result =
     mapOf("1" to 1, "2" to 2, "3" to 3)
       .unalign { it.leftIor() }
  //sampleEnd
  println(result)
}
