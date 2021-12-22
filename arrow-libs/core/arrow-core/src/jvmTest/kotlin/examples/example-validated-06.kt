// This file was automatically generated from Validated.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleValidated06

import arrow.core.Validated
import arrow.core.andThen

fun main() {
  Validated.Valid(5).andThen { Validated.Valid(10) } // Result: Valid(10)
  Validated.Valid(5).andThen { Validated.Invalid(10) } // Result: Invalid(10)
  Validated.Invalid(5).andThen { Validated.Valid(10) } // Result: Invalid(5)
}
