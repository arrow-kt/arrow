// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither45

import arrow.core.Either.Right
import arrow.core.Either.Left
import arrow.core.getOrElse

fun main() {
  Right(12).getOrElse { 17 } // Result: 12
  Left(12).getOrElse { 17 }  // Result: 17
}
