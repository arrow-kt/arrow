// This file was automatically generated from flow.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleFlow03

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.collect
import arrow.fx.coroutines.parMapUnordered

suspend fun main(): Unit {
  flowOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    .parMapUnordered { a ->
      delay(100)
      a
    }.toList() // [3, 5, 4, 6, 2, 8, 7, 1, 9, 10]
}
