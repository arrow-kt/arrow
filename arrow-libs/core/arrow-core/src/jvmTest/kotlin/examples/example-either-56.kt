// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither56

import arrow.core.rightIfNotNull

fun main() {
  "value".rightIfNotNull { "left" } // Right(b="value")
  null.rightIfNotNull { "left" }    // Left(a="left")
}
