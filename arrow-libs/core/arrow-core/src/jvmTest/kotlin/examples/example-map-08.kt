// This file was automatically generated from map.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleMap08

import arrow.core.*
import io.kotest.matchers.shouldBe

fun test() {
  mapOf("first" to "A:1", "second" to "B:2", "third" to "C:3").unzip { (_, e) ->
    e.split(":").let {
      it.first() to it.last()
    }
  } shouldBe Pair(
    mapOf("first" to "A", "second" to "B", "third" to "C"),
    mapOf("first" to "1", "second" to "2", "third" to "3")
  )
}
