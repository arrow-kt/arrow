// This file was automatically generated from ParZip.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleParzip05

import arrow.fx.coroutines.*
import kotlinx.coroutines.Dispatchers

suspend fun main(): Unit {
  //sampleStart
  val result: Triple<String, String, String> = parZip(
    Dispatchers.IO,
    { "First one is on ${Thread.currentThread().name}" },
    { "Second one is on ${Thread.currentThread().name}" },
    fc = { "Third one is on ${Thread.currentThread().name}" }
  )
  //sampleEnd
 println(result)
}
