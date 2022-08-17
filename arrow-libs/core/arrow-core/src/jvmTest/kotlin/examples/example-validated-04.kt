// This file was automatically generated from Validated.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleValidated04

import arrow.core.Validated

fun main() {
  Validated.Valid(12).tap { println("flower") } // Result: prints "flower" and returns: Valid(12)
  Validated.Invalid(12).tap { println("flower") }  // Result: Invalid(12)
}
