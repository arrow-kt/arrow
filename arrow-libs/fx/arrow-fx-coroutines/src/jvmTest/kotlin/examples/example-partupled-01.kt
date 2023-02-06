// This file was automatically generated from ParTupled.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.examplePartupled01

import arrow.fx.coroutines.*
import kotlinx.coroutines.Dispatchers

suspend fun main(): Unit {
  //sampleStart
  val result = parTupled(
    Dispatchers.IO,
    { "First one is on ${Thread.currentThread().name}" },
    { "Second one is on ${Thread.currentThread().name}" }
  )
  //sampleEnd
 println(result)
}
