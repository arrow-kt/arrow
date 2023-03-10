// This file was automatically generated from Iterable.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleIterable08

import arrow.core.*

val padRight = listOf(1, 2).rightPadZip(listOf("a"))        // Result: [Pair(1, "a"), Pair(2, null)]
val padLeft = listOf(1).rightPadZip(listOf("a", "b"))       // Result: [Pair(1, "a")]
val noPadding = listOf(1, 2).rightPadZip(listOf("a", "b"))  // Result: [Pair(1, "a"), Pair(2, "b")]

fun main() {
  println("padRight = $padRight")
  println("padLeft = $padLeft")
  println("noPadding = $noPadding")
}
