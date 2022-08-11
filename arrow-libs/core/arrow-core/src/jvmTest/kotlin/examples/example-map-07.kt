// This file was automatically generated from map.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleMap07

import arrow.core.*

fun main(args: Array<String>) {
  //sampleStart
  val result =
     mapOf("first" to ("A" to 1), "second" to ("B" to 2)).unzip()
  //sampleEnd
  println(result)
}
