// This file was automatically generated from map.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleMap04

import arrow.core.*
import io.kotest.matchers.shouldBe

fun test() {
  mapOf("1" to 1, "2" to 2)
    .align(mapOf("1" to 1, "2" to 2, "3" to 3)) { (_, a) ->
      "$a"
    } shouldBe mapOf("1" to "Ior.Both(1, 1)", "2" to Ior.Both(2, 2), "3" to Ior.Right(3))
}
