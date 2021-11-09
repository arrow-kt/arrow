package arrow

import arrow.core.Either

public suspend fun <E, A> either(f: suspend ContEffect<E>.() -> A): Either<E, A> =
  cont(f).toEither()
