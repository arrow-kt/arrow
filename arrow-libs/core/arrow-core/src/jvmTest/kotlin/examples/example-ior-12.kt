// This file was automatically generated from Ior.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleIor12

import arrow.core.Ior

fun main() {
  Ior.Right(12).toValidated() // Result: Valid(12)
  Ior.Left(12).toValidated()  // Result: Invalid(12)
  Ior.Both(12, "power").toValidated()  // Result: Valid("power")
}
