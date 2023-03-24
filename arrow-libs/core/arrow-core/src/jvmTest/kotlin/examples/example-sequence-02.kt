// This file was automatically generated from Sequence.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleSequence02

import arrow.core.align
import arrow.core.Ior.Both
import arrow.core.Ior.Left
import arrow.core.Ior.Right
import io.kotest.matchers.shouldBe

fun test() {
  sequenceOf("A", "B")
    .align(sequenceOf(1, 2, 3)).toList() shouldBe listOf(Both("A", 1), Both("B", 2), Right(3))

  sequenceOf("A", "B", "C")
    .align(sequenceOf(1, 2)).toList() shouldBe listOf(Both("A", 1), Both("B", 2), Left("C"))
}
