// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleArrowCoreEitherContains01

import arrow.core.Either.Right
import arrow.core.Either.Left
import arrow.core.contains

fun main() {
  Right("something").contains("something") // Result: true
  Right("something").contains("anything")  // Result: false
  Left("something").contains("something")  // Result: false
}
