// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither35

import arrow.core.*

fun main() {
  Either.Left("left").swap()   // Result: Right("left")
  Either.Right("right").swap() // Result: Left("right")
}
