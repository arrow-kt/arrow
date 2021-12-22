// This file was automatically generated from ParZip.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleParzip01

import arrow.fx.coroutines.*

suspend fun main(): Unit {
  //sampleStart
  val result = parZip(
    { "First one is on ${Thread.currentThread().name}" },
    { "Second one is on ${Thread.currentThread().name}" }
  ) { a, b ->
      "$a\n$b"
    }
  //sampleEnd
 println(result)
}
