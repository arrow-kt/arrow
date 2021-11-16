// This file was automatically generated from Sequence.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleSequence08

import arrow.core.padZip

val padZipRight = sequenceOf(1, 2).padZip(sequenceOf(3)) { l, r -> (l?:0) + (r?:0) }  // Result: [4, 2]
val padZipLeft = sequenceOf(1).padZip(sequenceOf(3, 4)) { l, r -> (l?:0) + (r?:0) }   // Result: [4, 4]
val noPadding = sequenceOf(1, 2).padZip(sequenceOf(3, 4)) { l, r -> (l?:0) + (r?:0) } // Result: [4, 6]

fun main() {
  println("padZipRight = $padZipRight")
  println("padZipLeft = $padZipLeft")
  println("noPadding = $noPadding")
}
