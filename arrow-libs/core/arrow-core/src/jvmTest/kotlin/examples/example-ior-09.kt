// This file was automatically generated from Ior.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleIor09

import arrow.core.Ior

fun main() {
  Ior.Right(12).toEither() // Result: Either.Right(12)
  Ior.Left(12).toEither()  // Result: Either.Left(12)
  Ior.Both("power", 12).toEither()  // Result: Either.Right(12)
}
