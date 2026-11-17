// This file was automatically generated from flow.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleFlow03

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import arrow.fx.coroutines.parMapUnordered
import kotlin.time.Duration.Companion.milliseconds

@OptIn(kotlinx.coroutines.FlowPreview::class, kotlinx.coroutines.ExperimentalCoroutinesApi::class)
suspend fun main() {
  flowOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    .parMapUnordered { a ->
      delay(100.milliseconds)
      a
    }.toList() // [3, 5, 4, 6, 2, 8, 7, 1, 9, 10]
}
