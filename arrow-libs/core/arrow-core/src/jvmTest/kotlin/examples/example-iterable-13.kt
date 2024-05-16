// This file was automatically generated from Iterable.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleIterable13

import arrow.core.*
import io.kotest.matchers.shouldBe

fun test() {
  emptyList<Int>().split() shouldBe null
  listOf("A", "B", "C").split() shouldBe Pair(listOf("B", "C"), "A")
}
