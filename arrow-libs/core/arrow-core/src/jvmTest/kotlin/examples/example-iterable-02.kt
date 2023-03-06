// This file was automatically generated from Iterable.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleIterable02

import arrow.core.left
import arrow.core.leftNel
import arrow.core.nonEmptyListOf
import arrow.core.mapOrAccumulate
import io.kotest.matchers.shouldBe

fun main() {
  listOf(1, 2, 3, 4).mapOrAccumulate { i ->
    when(i) {
      1 -> "Either - $i".left().bind()
      2 -> "Either - $i".leftNel().bindNel()
      3 -> raise("Raise - $i")
      else -> withNel { raise(nonEmptyListOf("RaiseNel - $i")) }
    }
  } shouldBe nonEmptyListOf("Either - 1", "EitherNel - 2", "Raise - 3", "RaiseNel - 4").left()
}
