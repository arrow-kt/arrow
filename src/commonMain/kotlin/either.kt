import arrow.core.Either

suspend fun <E, A> either(f: suspend ContEffect<E, A>.() -> A): Either<E, A> =
  cont(f).fold({ Either.Left(it) }) { Either.Right(it) }
