// This file was automatically generated from ErrorHandlers.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEffectError01

import arrow.core.continuations.effect
import arrow.core.continuations.catch

object User
object Error

val error = effect<Error, User> { shift(Error) } // // Shift(error)

val a = error.catch<Error, Error, User> { error -> User } // Success(User)
val b = error.catch<Error, String, User> { error -> shift("other-failure") } // Shift(other-failure)
val c = error.catch<Error, Nothing, User> { error -> throw RuntimeException("BOOM") } // Exception(BOOM)
