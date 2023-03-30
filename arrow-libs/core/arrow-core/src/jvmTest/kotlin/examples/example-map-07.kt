// This file was automatically generated from map.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleMap07

import arrow.core.*
import io.kotest.matchers.shouldBe

fun test() {
  mapOf(
    "first" to ("A" to 1),
    "second" to ("B" to 2)
  ).unzip() shouldBe Pair(mapOf("first" to "A", "second" to "B"), mapOf("first" to 1, "second" to 2))
}
