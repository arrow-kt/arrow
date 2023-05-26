// This file was automatically generated from ErrorHandlers.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEffectError04

import arrow.core.raise.effect
import arrow.core.raise.mapError

object User
object Error

val error = effect<Error, User> { raise(Error) } // Raise(error)

val a = error.mapError<Error, String, User> { error -> "some-failure" } // Raise(some-failure)
val b = error.mapError<Error, String, User> { error -> raise("other-failure") } // Raise(other-failure)
val c = error.mapError<Error, Nothing, User> { error -> throw RuntimeException("BOOM") } // Exception(BOOM)
