// This file was automatically generated from Iterable.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleIterable16

import arrow.core.*
import io.kotest.matchers.shouldBe

fun test() {
  val list1 = listOf(1, 2, 3)
  val list2 = listOf(4, 5, 6, 7, 8)
  list1.interleave(list2) shouldBe listOf(1, 4, 2, 5, 3, 6, 7, 8)
}
