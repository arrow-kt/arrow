// This file was automatically generated from Effect.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleRaise03

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.raise.Effect
import arrow.core.raise.fold
import arrow.core.identity

suspend fun <R, A> Effect<R, A>.toEither(): Either<R, A> =
  fold({ Either.Left(it) }) { Either.Right(it) }

suspend fun <A> Effect<Option<Nothing>, A>.toOption(): Option<A> =
  fold(::identity) { Some(it) }
