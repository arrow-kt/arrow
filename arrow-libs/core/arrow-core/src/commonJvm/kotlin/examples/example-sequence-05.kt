// This file was automatically generated from Sequence.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleSequence05

import arrow.core.leftPadZip

val left = sequenceOf(1, 2).leftPadZip(sequenceOf(3)) { l, r -> l?.plus(r) ?: r }    // Result: [4]
val right = sequenceOf(1).leftPadZip(sequenceOf(3, 4)) { l, r -> l?.plus(r) ?: r }   // Result: [4, 4]
val both = sequenceOf(1, 2).leftPadZip(sequenceOf(3, 4)) { l, r -> l?.plus(r) ?: r } // Result: [4, 6]

fun main() {
  println("left = $left")
  println("right = $right")
  println("both = $both")
}
