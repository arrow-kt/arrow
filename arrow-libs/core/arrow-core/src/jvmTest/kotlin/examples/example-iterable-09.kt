// This file was automatically generated from Iterable.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleIterable09

import arrow.core.*
import io.kotest.matchers.shouldBe

fun test() {
  listOf("A", "B").align(listOf(1, 2, 3)) {
     "$it"
  } shouldBe listOf("Ior.Both(A, 1)", "Ior.Both(B, 2)", "Ior.Right(3)")
}
