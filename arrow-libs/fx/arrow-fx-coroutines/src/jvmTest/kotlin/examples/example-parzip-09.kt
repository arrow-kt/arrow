// This file was automatically generated from ParZip.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleParzip09

import arrow.fx.coroutines.*

suspend fun main(): Unit {
  //sampleStart
  val result = parZip(
    { "First one is on ${Thread.currentThread().name}" },
    { "Second one is on ${Thread.currentThread().name}" },
    { "Third one is on ${Thread.currentThread().name}" },
    { "Fourth one is on ${Thread.currentThread().name}" },
    { "Fifth one is on ${Thread.currentThread().name}" },
    { "Sixth one is on ${Thread.currentThread().name}" }
  ) { a, b, c, d, e, f ->
      "$a\n$b\n$c\n$d\n$e\n$f"
    }
  //sampleEnd
 println(result)
}
