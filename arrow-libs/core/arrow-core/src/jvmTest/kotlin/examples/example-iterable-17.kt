// This file was automatically generated from Iterable.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleIterable17

import arrow.core.*
import io.kotest.matchers.shouldBe

fun test() {
  val ints = listOf(1, 2)
  val res = ints.unweave { i -> listOf(i, i + 1, i + 2) }
  res shouldBe listOf(1, 2, 2, 3, 3, 4)
  res shouldBe ints.interleave(ints.flatMap { listOf(it + 1, it + 2) })
}
