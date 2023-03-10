// This file was automatically generated from Iterable.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleIterable04

import arrow.core.*
import io.kotest.matchers.shouldBe

fun test() {
  listOf(1, 2).padZip(listOf("a")) { l, r -> l to r } shouldBe listOf(1 to "a", 2 to null)
  listOf(1).padZip(listOf("a", "b")) { l, r -> l to r } shouldBe listOf(1 to "a", null to "b")
  listOf(1).padZip(listOf("a", "b")) { l, r -> l to r } shouldBe listOf(1 to "a", null to "b")
}
