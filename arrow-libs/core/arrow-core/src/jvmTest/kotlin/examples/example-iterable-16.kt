// This file was automatically generated from Iterable.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleIterable16

import arrow.core.*
import io.kotest.matchers.shouldBe

fun test() {
  listOf("A".left(), 2.right(), "C".left(), 4.right())
    .separateEither() shouldBe Pair(listOf("A", "C"), listOf(2, 4))
}
