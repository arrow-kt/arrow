// This file was automatically generated from flow.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleFlow01

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.collect
import arrow.fx.coroutines.parMap

suspend fun main(): Unit {
  flowOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    .parMap { a ->
      delay(100)
      a
    }.toList() // [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
}
