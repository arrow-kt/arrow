// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither49

import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.merge

fun main() {
  Right(12).merge() // Result: 12
  Left(12).merge() // Result: 12
}
