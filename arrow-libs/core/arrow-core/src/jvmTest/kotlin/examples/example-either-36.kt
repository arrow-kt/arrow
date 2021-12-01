// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither36

import arrow.core.*

fun main() {
  Either.Right(12).map { "flower" } // Result: Right("flower")
  Either.Left(12).map { "flower" }  // Result: Left(12)
}
