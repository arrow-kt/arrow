// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither53

import arrow.core.Either.*
import arrow.core.leftIfNull

fun main() {
  Right(12).leftIfNull({ -1 })   // Result: Right(12)
  Right(null).leftIfNull({ -1 }) // Result: Left(-1)

  Left(12).leftIfNull({ -1 })    // Result: Left(12)
}
