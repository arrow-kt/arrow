// This file was automatically generated from Iterable.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleIterable12

import arrow.core.*

fun main(args: Array<String>) {
  //sampleStart
  val result =
   listOf("A:1", "B:2", "C:3").unzip { e ->
     e.split(":").let {
       it.first() to it.last()
     }
   }
  //sampleEnd
  println(result)
}
