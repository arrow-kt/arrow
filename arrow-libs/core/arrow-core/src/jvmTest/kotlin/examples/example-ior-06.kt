// This file was automatically generated from Ior.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleIor06

import arrow.core.Ior

fun main() {
  Ior.Left("left").swap()   // Result: Right("left")
  Ior.Right("right").swap() // Result: Left("right")
  Ior.Both("left", "right").swap() // Result: Both("right", "left")
}
