// This file was automatically generated from Iterable.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleIterable04

import arrow.core.*

val padRight = listOf(1, 2).leftPadZip(listOf("a"))        // Result: [Pair(1, "a")]
val padLeft = listOf(1).leftPadZip(listOf("a", "b"))       // Result: [Pair(1, "a"), Pair(null, "b")]
val noPadding = listOf(1, 2).leftPadZip(listOf("a", "b"))  // Result: [Pair(1, "a"), Pair(2, "b")]

fun main() {
  println("left = $left")
  println("right = $right")
  println("both = $both")
}
