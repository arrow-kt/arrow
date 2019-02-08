package arrow.typeclasses.suspended

import arrow.Kind
import arrow.core.Either
import arrow.core.OptionOf
import arrow.core.TryOf
import arrow.typeclasses.ApplicativeError

interface ApplicativeErrorSyntax<F, E> : ApplicativeError<F, E>, ApplicativeSyntax<F> {

  suspend fun <A> handleError(fa: suspend () -> A, recover: suspend (E) -> A): Kind<F, A> =
    run<ApplicativeError<F, E>, Kind<F, A>> { fa.effect().handleErrorWith(recover.flatLiftM()) }

  suspend fun <A> OptionOf<A>.getOrRaiseError(f: () -> E): Kind<F, A> =
    run<ApplicativeError<F, E>, Kind<F, A>> { this@getOrRaiseError.fromOption(f) }

  suspend fun <A, B> Either<B, A>.getOrRaiseError(f: (B) -> E): Kind<F, A> =
    run<ApplicativeError<F, E>, Kind<F, A>> { this@getOrRaiseError.fromEither(f) }

  suspend fun <A> TryOf<A>.getOrRaiseError(f: (Throwable) -> E): Kind<F, A> =
    run<ApplicativeError<F, E>, Kind<F, A>> { this@getOrRaiseError.fromTry(f) }

  suspend fun <A> attempt(fa: suspend () -> A): Kind<F, Either<E, A>> =
    run<ApplicativeError<F, E>, Kind<F, Either<E, A>>> { fa.effect().attempt() }

}