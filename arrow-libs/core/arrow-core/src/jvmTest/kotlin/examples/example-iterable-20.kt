// This file was automatically generated from Iterable.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleIterable20

import arrow.core.*
import io.kotest.matchers.shouldBe

fun test() {
  listOf(1, 2, 3, 4)
    .partitionMap {
      if (it % 2 == 0) "even: $it".right() else "odd: $it".left()
    } shouldBe Pair(listOf("odd: 1", "odd: 3"), listOf("even: 2", "even: 4"))
}
