// This file was automatically generated from ErrorHandlers.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEffectError02

import arrow.core.continuations.effect
import arrow.core.continuations.attempt

object User
object Error

val exception = effect<Error, User> { throw RuntimeException("BOOM") }  // Exception(BOOM)

val a = exception.attempt { error -> error.message?.length ?: -1 } // Success(5)
val b = exception.attempt { shift(Error) } // Shift(error)
val c = exception.attempt { throw  RuntimeException("other-failure") } // Exception(other-failure)
