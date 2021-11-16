// This file was automatically generated from Ior.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleIor21

import arrow.core.Ior

val right = Ior.Right(12).leftOrNull()         // Result: null
val left = Ior.Left(12).leftOrNull()           // Result: 12
val both = Ior.Both(12, "power").leftOrNull()  // Result: 12

fun main() {
  println("right = $right")
  println("left = $left")
  println("both = $both")
}
