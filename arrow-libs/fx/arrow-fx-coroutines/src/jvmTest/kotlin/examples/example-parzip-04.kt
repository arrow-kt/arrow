// This file was automatically generated from ParZip.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleParzip04

import arrow.fx.coroutines.*
import kotlinx.coroutines.Dispatchers

suspend fun main(): Unit {
  //sampleStart
  val result = parZip(
    Dispatchers.IO,
    { "First one is on ${Thread.currentThread().name}" },
    { "Second one is on ${Thread.currentThread().name}" },
    { "Third one is on ${Thread.currentThread().name}" }
  ) { a, b, c ->
      "$a\n$b\n$c"
    }
  //sampleEnd
 println(result)
}
