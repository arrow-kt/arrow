// This file was automatically generated from Iterable.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleIterable05

import arrow.core.*

val left = listOf(1, 2).leftPadZip(listOf("a")) { l, r -> l to r }      // Result: [Pair(1, "a")]
val right = listOf(1).leftPadZip(listOf("a", "b")) { l, r -> l to r }   // Result: [Pair(1, "a"), Pair(null, "b")]
val both = listOf(1, 2).leftPadZip(listOf("a", "b")) { l, r -> l to r } // Result: [Pair(1, "a"), Pair(2, "b")]

fun main() {
  println("left = $left")
  println("right = $right")
  println("both = $both")
}
