// This file was automatically generated from Iterable.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleIterable11

import arrow.core.*
import io.kotest.matchers.shouldBe

fun test() {
  listOf("A" to 1, "B" to 2)
    .unzip() shouldBe Pair(listOf("A", "B"), listOf(1, 2))
}
