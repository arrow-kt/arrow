// This file was automatically generated from map.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleMap06

import arrow.core.*
import io.kotest.matchers.shouldBe

fun test() {
  mapOf("1" to 1, "2" to 2, "3" to 3)
    .unalign { (key, value) ->
      when(key) {
        "1" -> Ior.Left(value)
        "2" -> Ior.Right(key)
        else -> Ior.Both(value, key)
      }
    } shouldBe Pair(mapOf("1" to 1, "3" to 3), mapOf("2" to 2, "3" to 3))
}
