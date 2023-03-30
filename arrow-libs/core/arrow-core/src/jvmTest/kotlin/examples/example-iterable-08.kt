// This file was automatically generated from Iterable.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleIterable08

import arrow.core.*
import io.kotest.matchers.shouldBe

fun test() {
  listOf(1, 2).rightPadZip(listOf("a")) shouldBe listOf(1 to "a", 2 to null)
  listOf(1).rightPadZip(listOf("a", "b")) shouldBe listOf(1 to "a")
  listOf(1, 2).rightPadZip(listOf("a", "b")) shouldBe listOf(1 to "a", 2 to "b")
}
