package arrow.syntax.applicativeerror

import arrow.*
import arrow.core.Either
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.applicativeError

inline fun <reified F, reified E, A> A.raiseError(FT: ApplicativeError<F, E> = applicativeError(), e: E): Kind<F, A> = FT.raiseError<A>(e)

inline fun <reified F, reified E, A> Kind<F, A>.handlerErrorWith(FT: ApplicativeError<F, E> = applicativeError(), noinline f: (E) -> Kind<F, A>): Kind<F, A> =
        FT.handleErrorWith(this, f)

inline fun <reified F, reified E, A> Kind<F, A>.attempt(FT: ApplicativeError<F, E> = applicativeError()): Kind<F, Either<E, A>> = FT.attempt(this)

inline fun <reified F, reified E, A> Either<E, A>.toError(FT: ApplicativeError<F, E> = applicativeError()): Kind<F, A> = FT.fromEither(this)

inline fun <reified F, A> (() -> A).catch(FT: ApplicativeError<F, Throwable> = applicativeError()): Kind<F, A> = FT.catch(this)

fun <F, A> ApplicativeError<F, Throwable>.catch(f: () -> A): Kind<F, A> =
        try {
            pure(f())
        }
        catch (e: Throwable) {
            raiseError(e)
        }