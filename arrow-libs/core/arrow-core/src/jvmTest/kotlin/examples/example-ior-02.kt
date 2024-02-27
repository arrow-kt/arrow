// This file was automatically generated from Ior.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleIor02

import arrow.core.Ior

fun main() {
  Ior.Left("tulip").isRight()           // Result: false
  Ior.Right("venus fly-trap").isRight() // Result: true
  Ior.Both("venus", "fly-trap").isRight() // Result: false
}
