// This file was automatically generated from Iterable.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleIterable05

import arrow.core.*
import io.kotest.matchers.shouldBe

fun test() {
  listOf(1, 2).leftPadZip(listOf("a")) { l, r -> l to r } shouldBe listOf(1 to "a")
  listOf(1).leftPadZip(listOf("a", "b")) { l, r -> l to r } shouldBe listOf(1 to "a", null to "b")
  listOf(1, 2).leftPadZip(listOf("a", "b")) { l, r -> l to r } shouldBe listOf(1 to "a", 2 to "b")
}
