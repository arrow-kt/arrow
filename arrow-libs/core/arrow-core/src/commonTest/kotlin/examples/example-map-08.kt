// This file was automatically generated from map.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleMap08

import arrow.core.*

fun main(args: Array<String>) {
  //sampleStart
  val result =
   mapOf("first" to "A:1", "second" to "B:2", "third" to "C:3").unzip { (_, e) ->
     e.split(":").let {
       it.first() to it.last()
     }
   }
  //sampleEnd
  println(result)
}
