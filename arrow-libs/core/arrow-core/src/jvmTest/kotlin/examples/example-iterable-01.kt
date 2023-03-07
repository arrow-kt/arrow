// This file was automatically generated from Iterable.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleIterable01

import arrow.core.left
import arrow.core.leftNel
import arrow.core.nonEmptyListOf
import arrow.core.mapOrAccumulate
import io.kotest.matchers.shouldBe

fun test() {
  listOf(1, 2, 3, 4).mapOrAccumulate({ a, b -> "$a, $b" }) { i ->
    when(i) {
      1 -> "Either - $i".left().bind()
      2 -> "EitherNel - $i".leftNel().bindNel()
      3 -> raise("Raise - $i")
      else -> withNel { raise(nonEmptyListOf("RaiseNel - $i")) }
    }
  } shouldBe "Either - 1, EitherNel - 2, Raise - 3, RaiseNel - 4".left()
}
