// This file was automatically generated from Iterable.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleIterable12

import arrow.core.*
import io.kotest.matchers.shouldBe

fun test() {
  listOf("A:1", "B:2", "C:3").unzip { e ->
    e.split(":").let {
      it.first() to it.last()
    }
  } shouldBe Pair(listOf("A", "B", "C"), listOf("1", "2", "3"))
}
