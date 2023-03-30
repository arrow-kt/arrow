// This file was automatically generated from Sequence.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleSequence04

import arrow.core.*
import io.kotest.matchers.shouldBe

fun test() {
  val tags = generateSequence { "#" }.take(5)
  val numbers = generateSequence(0) { it + 1 }.take(3)
  tags.interleave(numbers).toList() shouldBe listOf("#", 0, "#", 1, "#", 2, "#", "#")
}
