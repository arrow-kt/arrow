// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither50

import arrow.core.Either.Right
import arrow.core.Either.Left

fun main() {
  Right(12).orNull() // Result: 12
  Left(12).orNull()  // Result: null
}
