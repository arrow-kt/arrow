package arrow.syntax.either

import arrow.core.Either

fun <A> A.left(): Either<A, Nothing> = Either.Left(this)

fun <A> A.right(): Either<Nothing, A> = Either.Right(this)

