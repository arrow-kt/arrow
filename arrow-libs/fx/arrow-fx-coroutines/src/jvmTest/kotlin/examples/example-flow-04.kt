// This file was automatically generated from flow.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleFlow04

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.collect
import arrow.fx.coroutines.parMapNotNullUnordered

suspend fun main(): Unit {
  flowOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    .parMapNotNullUnordered { a ->
      delay(100)
      a.takeIf { a % 2 == 0 }
    }.toList() // [4, 6, 2, 8, 10]
}
