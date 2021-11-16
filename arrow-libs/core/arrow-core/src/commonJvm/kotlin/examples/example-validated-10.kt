// This file was automatically generated from Validated.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleValidated10

import arrow.core.Validated
import arrow.core.validNel
import arrow.core.zip

val parallelValidate =
  1.validNel().zip(2.validNel())
    { a, b -> /* combine the result */ }
