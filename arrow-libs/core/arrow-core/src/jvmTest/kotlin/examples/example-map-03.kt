// This file was automatically generated from map.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleMap03

import arrow.core.*
import io.kotest.matchers.shouldBe

fun test() {
  val res = mapOf(1 to 1, 2 to 2).align(mapOf(1 to "1", 2 to "2", 3 to "3"))
  res shouldBe mapOf(1 to Ior.Both(1, "1"), 2 to Ior.Both(2, "2"), 3 to Ior.Right("3"))
}
