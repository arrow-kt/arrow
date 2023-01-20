// This file was automatically generated from ParZip.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleParzip23

import arrow.fx.coroutines.*
import kotlinx.coroutines.Dispatchers

suspend fun main(): Unit {
  //sampleStart
  val result = parZip(
    Dispatchers.IO,
    { "First one is on ${Thread.currentThread().name}" },
    { "Second one is on ${Thread.currentThread().name}" }
    { "Third one is on ${Thread.currentThread().name}" }
    { "Fourth one is on ${Thread.currentThread().name}" }
    { "Fifth one is on ${Thread.currentThread().name}" }
    { "Sixth one is on ${Thread.currentThread().name}" }
    { "Seventh one is on ${Thread.currentThread().name}" }
    { "Eighth one is on ${Thread.currentThread().name}" }
    { "Ninth one is on ${Thread.currentThread().name}" }
  )
  //sampleEnd
 println(result)
}
