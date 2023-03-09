// This file was automatically generated from Iterable.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleIterable04

import arrow.core.*

val padZipRight = listOf(1, 2).padZip(listOf("a")) { l, r -> l to r }     // Result: [Pair(1, "a"), Pair(2, null)]
val padZipLeft = listOf(1).padZip(listOf("a", "b")) { l, r -> l to r }    // Result: [Pair(1, "a"), Pair(null, "b")]
val noPadding = listOf(1, 2).padZip(listOf("a", "b")) { l, r -> l to r }  // Result: [Pair(1, "a"), Pair(2, "b")]

fun main() {
  println("padZipRight = $padZipRight")
  println("padZipLeft = $padZipLeft")
  println("noPadding = $noPadding")
}
