// This file was automatically generated from ErrorHandlers.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEffectError05

import arrow.core.raise.eagerEffect
import arrow.core.raise.mapError

object User
object Error

val error = eagerEffect<Error, User> { raise(Error) } // Raise(error)

val a = error.mapError<Error, String, User> { error -> "some-failure" } // Raise(some-failure)
val b = error.mapError<Error, String, User>(Any::toString) // Raise(Error)
