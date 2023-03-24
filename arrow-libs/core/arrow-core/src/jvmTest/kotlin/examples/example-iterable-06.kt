// This file was automatically generated from Iterable.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleIterable06

import arrow.core.*
import io.kotest.matchers.shouldBe

fun test() {
  listOf(1, 2).leftPadZip(listOf("a")) shouldBe listOf(1 to "a")
  listOf(1).leftPadZip(listOf("a", "b")) shouldBe listOf(1 to "a", null to "b")
  listOf(1, 2).leftPadZip(listOf("a", "b")) shouldBe listOf(1 to "a", 2 to "b")
}
