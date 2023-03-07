// This file was automatically generated from Sequence.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleSequence01

import arrow.core.align
import arrow.core.Ior.Both
import arrow.core.Ior.Left
import arrow.core.Ior.Right
import io.kotest.matchers.shouldBe

fun test() {
  fun Ior<String, Int>.visualise(): String =
    fold({ "$it<" }, { ">$it" }, { a, b -> "$a<>$b" })

  sequenceOf("A", "B").align(sequenceOf(1, 2, 3)) { ior ->
    ior.visualise()
  }.toList() shouldBe listOf("A<>1", "B<>2", ">3")

  sequenceOf("A", "B", "C").align(sequenceOf(1, 2)) { ior ->
    ior.visualise()
  }.toList() shouldBe listOf("A<>1", "B<>2", "C<")
}
