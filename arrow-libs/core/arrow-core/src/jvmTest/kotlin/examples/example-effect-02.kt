// This file was automatically generated from Effect.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEffect02

import arrow.core.continuations.effect
import arrow.core.continuations.catch

object User
object Error

val error = effect<Error, User> { shift(Error) } // // Shift(error)
val a = error.catch<Error, Int, User>({ shift(-1) }) { _: Error -> User } // Success(User)
val b = error.catch<Error, Int, User>({ User }) { _: Error -> shift(5) } // Shift(5)
val c = error.catch<Error, Int, User>({ User }) { _: Error -> throw RuntimeException("5") } // Exception(5)

val exception = effect<Error, User> { throw RuntimeException("BOOM") }  // Exception(BOOM)*
val d = exception.catch<Error, Int, User>({ _: Throwable -> User }) { shift(-1) } // Success(User)
val e = exception.catch<Error, Int, User>({ _: Throwable -> shift(5) }) { User }  // Shift(5)
val f = exception.catch<Error, Int, User>({ throw RuntimeException("5") }) { User } // Exception(5)
