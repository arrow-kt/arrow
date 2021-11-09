// This file was automatically generated from README.md by Knit tool. Do not edit.
package example.exampleReadme03

import arrow.Cont
import arrow.core.identity
import arrow.core.Either
import arrow.core.Option
import arrow.core.None
import arrow.core.Some

suspend fun <R, A> Cont<R, A>.toEither(): Either<R, A> =
  fold({ Either.Left(it) }) { Either.Right(it) }

suspend fun <A> Cont<None, A>.toOption(): Option<A> =
  fold(::identity) { Some(it) }
