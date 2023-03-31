// This file was automatically generated from map.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleMap05

import arrow.core.*
import io.kotest.matchers.shouldBe

fun test() {
  mapOf(
    "first" to Ior.Both("A", 1),
    "second" to Ior.Both("B", 2),
    "third" to Ior.Left("C")
  ).unalign() shouldBe Pair(mapOf("first" to "A", "second" to "B", "third" to "C"), mapOf("first" to 1, "second" to 2))
}
