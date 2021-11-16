// This file was automatically generated from Ior.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleIor20

import arrow.core.Ior

val right = Ior.Right(12).orNull()         // Result: 12
val left = Ior.Left(12).orNull()           // Result: null
val both = Ior.Both(12, "power").orNull()  // Result: "power"
fun main() {
  println("right = $right")
  println("left = $left")
  println("both = $both")
}
