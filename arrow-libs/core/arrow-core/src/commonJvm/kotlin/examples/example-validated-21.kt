// This file was automatically generated from Validated.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleValidated21

import arrow.core.Validated
Validated.Valid(5).andThen { Valid(10) } // Result: Valid(10)
Validated.Valid(5).andThen { Invalid(10) } // Result: Invalid(10)
Validated.Invalid(5).andThen { Valid(10) } // Result: Invalid(5)
