// This file was automatically generated from Validated.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleValidated09

import arrow.core.Validated
import arrow.core.validNel
import arrow.core.zip
import arrow.typeclasses.Semigroup

val parallelValidate =
   1.validNel().zip(Semigroup.nonEmptyList<ConfigError>(), 2.validNel())
    { a, b -> /* combine the result */ }
