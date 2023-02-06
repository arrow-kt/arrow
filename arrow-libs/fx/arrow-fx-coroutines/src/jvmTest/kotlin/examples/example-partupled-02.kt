// This file was automatically generated from ParTupled.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.examplePartupled02

import arrow.fx.coroutines.*
import kotlinx.coroutines.Dispatchers

suspend fun main(): Unit {
  //sampleStart
  val result: Triple<String, String, String> = parTupled(
    Dispatchers.IO,
    { "First one is on ${Thread.currentThread().name}" },
    { "Second one is on ${Thread.currentThread().name}" },
    { "Third one is on ${Thread.currentThread().name}" }
  )
  //sampleEnd
 println(result)
}
