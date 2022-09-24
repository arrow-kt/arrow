// This file was automatically generated from ErrorHandlers.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEffectError01

import arrow.core.continuations.effect
import arrow.core.continuations.recover

object User
object Error

val error = effect<Error, User> { raise(Error) } // // Shift(error)

val a = error.recover<Error, Error, User> { error -> User } // Success(User)
val b = error.recover<Error, String, User> { error -> raise("other-failure") } // Shift(other-failure)
val c = error.recover<Error, Nothing, User> { error -> throw RuntimeException("BOOM") } // Exception(BOOM)
