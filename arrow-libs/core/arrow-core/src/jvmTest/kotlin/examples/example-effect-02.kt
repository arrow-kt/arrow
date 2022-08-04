// This file was automatically generated from Effect.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEffect02

import arrow.core.continuations.effect

object User
object Error

val error = effect<Error, User> { shift(Error) } // // Shift(error)

val a = error.catch { User } // Success(User)
val b = error.catch { shift("other-failure") } // Shift(other-failure)
val c = error.catch { throw RuntimeException("BOOM") } // Exception(BOOM)
