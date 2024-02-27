// This file was automatically generated from Ior.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleIor01

import arrow.core.Ior

fun main() {
  Ior.Left("tulip").isLeft()           // Result: true
  Ior.Right("venus fly-trap").isLeft() // Result: false
  Ior.Both("venus", "fly-trap").isLeft() // Result: false
}
