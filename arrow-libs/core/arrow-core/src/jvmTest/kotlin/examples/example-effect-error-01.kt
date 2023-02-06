// This file was automatically generated from ErrorHandlers.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEffectError01

import arrow.core.raise.effect
import arrow.core.raise.recover

object User
object Error

val error = effect<Error, User> { raise(Error) } // Raise(error)

val a = error.recover<Error, Error, User> { error -> User } // Success(User)
val b = error.recover<Error, String, User> { error -> raise("other-failure") } // Raise(other-failure)
val c = error.recover<Error, Nothing, User> { error -> throw RuntimeException("BOOM") } // Exception(BOOM)
