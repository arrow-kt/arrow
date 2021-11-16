// This file was automatically generated from map.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleMap04

import arrow.core.*

fun main(args: Array<String>) {
  //sampleStart
  val result =
   mapOf("1" to 1, "2" to 2).align(mapOf("1" to 1, "2" to 2, "3" to 3)) { (_, a) ->
     "$a"
   }
  //sampleEnd
  println(result)
}
