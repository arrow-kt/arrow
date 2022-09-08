// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither46

import arrow.core.Either.Right
import arrow.core.Either.Left
import arrow.core.getOrHandle

fun main() {
  Right(12).getOrHandle { 17 } // Result: 12
  Left(12).getOrHandle { it + 5 } // Result: 17
}
