import arrow.core.Either

suspend fun <E, A> either(f: suspend ContEffect<E>.() -> A): Either<E, A> =
    cont(f).toEither()
