// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither44

import arrow.core.Either
import arrow.core.left
import arrow.core.recover

object User
object Error

val error: Either<Error, User> = Error.left()

val a: Either<Error, User> = error.recover { error -> User } // Either.Right(User)
val b: Either<String, User> = error.recover { error -> shift("other-failure") } // Either.Left(other-failure)
val c: Either<Nothing, User> = error.recover { error -> User } // Either.Right(User)
