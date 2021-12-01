// This file was automatically generated from Validated.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleValidated03

import arrow.core.Validated

fun main() {
  Validated.Valid(12).tapInvalid { println("flower") } // Result: Valid(12)
  Validated.Invalid(12).tapInvalid { println("flower") }  // Result: prints "flower" and returns: Invalid(12)
}
