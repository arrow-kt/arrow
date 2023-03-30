// This file was automatically generated from map.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleMap01

import arrow.core.zip
import io.kotest.matchers.shouldBe

fun test() {
  mapOf(1 to "A", 2 to "B")
    .zip(mapOf(1 to "1", 2 to "2", 3 to "3")) shouldBe mapOf(1 to Pair("A", "1"), 2 to Pair("B", "2"))
}
