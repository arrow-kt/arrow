// This file was automatically generated from Effect.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEffect03

import arrow.core.continuations.effect

object User
object Error

val exception = effect<Error, User> { throw RuntimeException("BOOM") }  // Exception(BOOM)

val a = exception.attempt { _ -> User } // Success(User)
val b = exception.attempt { _ -> shift(Error) } // Shift(error)
val c = exception.attempt { _ -> throw  RuntimeException("other-failure") } // Exception(other-failure)
