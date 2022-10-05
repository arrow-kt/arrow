// This file was automatically generated from ErrorHandlers.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEffectError02

import arrow.core.continuations.effect
import arrow.core.continuations.catch

object User
object Error

val exception = effect<Error, User> { throw RuntimeException("BOOM") }  // Exception(BOOM)

val a = exception.catch { error -> error.message?.length ?: -1 } // Success(5)
val b = exception.catch { raise(Error) } // Raise(error)
val c = exception.catch { throw  RuntimeException("other-failure") } // Exception(other-failure)
