// This file was automatically generated from Effect.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEffect01

import arrow.core.continuations.effect

object User
object Error

val error = effect<Error, User> { shift(Error) } // // Shift(error)
val a = error.catch({ shift(-1) }) { User } // Success(User)
val b = error.catch({ User }) { shift(5) } // Shift(5)
val c = error.catch({ User }) { throw RuntimeException("5") } // Exception(5)

val exception = effect<Error, User> { throw RuntimeException("BOOM") }  // Exception(BOOM)*
val d = exception.catch({ User }) { shift(-1) } // Success(User)
val e = exception.catch({ shift(5) }) { User }  // Shift(5)
val f = exception.catch({ throw RuntimeException("5") }) { User } // Exception(5)
