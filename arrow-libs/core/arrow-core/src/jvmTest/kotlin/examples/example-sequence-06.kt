// This file was automatically generated from Sequence.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleSequence06

import arrow.core.leftPadZip

val padRight = sequenceOf(1, 2).leftPadZip(sequenceOf("a"))        // Result: [Pair(1, "a")]
val padLeft = sequenceOf(1).leftPadZip(sequenceOf("a", "b"))       // Result: [Pair(1, "a"), Pair(null, "b")]
val noPadding = sequenceOf(1, 2).leftPadZip(sequenceOf("a", "b"))  // Result: [Pair(1, "a"), Pair(2, "b")]

fun main() {
  println("padRight = $padRight")
  println("padLeft = $padLeft")
  println("noPadding = $noPadding")
}
