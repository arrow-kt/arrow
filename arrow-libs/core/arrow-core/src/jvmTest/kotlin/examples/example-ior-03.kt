// This file was automatically generated from Ior.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleIor03

import arrow.core.Ior

fun main() {
  Ior.Left("tulip").isBoth           // Result: false
  Ior.Right("venus fly-trap").isBoth // Result: false
  Ior.Both("venus", "fly-trap").isBoth // Result: true
}
