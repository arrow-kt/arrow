// This file was automatically generated from Sequence.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleSequence10

import arrow.core.split
import io.kotest.matchers.shouldBe

fun test() {
  sequenceOf("A", "B", "C").split()?.let { (tail, head) ->
    head shouldBe "A"
    tail.toList() shouldBe listOf("B", "C")
  }
  emptySequence<String>().split() shouldBe null
}
