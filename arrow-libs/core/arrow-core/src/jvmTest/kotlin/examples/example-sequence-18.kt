// This file was automatically generated from Sequence.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleSequence18

import arrow.core.None
import arrow.core.Some
import arrow.core.filterOption
import io.kotest.matchers.shouldBe

fun test() {
generateSequence(0) { it + 1 }
  .map { if (it % 2 == 0) Some(it) else None }
  .filterOption()
  .take(5)
  .toList() shouldBe listOf(0, 2, 4, 6, 8)
}
