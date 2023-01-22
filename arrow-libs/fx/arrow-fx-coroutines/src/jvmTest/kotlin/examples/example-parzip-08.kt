// This file was automatically generated from ParZip.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleParzip08

import arrow.fx.coroutines.*
import kotlinx.coroutines.Dispatchers

suspend fun main(): Unit {
  //sampleStart
  val result = parZip(
    Dispatchers.IO,
    { "First one is on ${Thread.currentThread().name}" },
    { "Second one is on ${Thread.currentThread().name}" },
    { "Third one is on ${Thread.currentThread().name}" },
    fd = { "Fourth one is on ${Thread.currentThread().name}" }
  )
  //sampleEnd
 println(result)
}
