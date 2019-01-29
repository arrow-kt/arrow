package arrow.typeclasses.suspended

import arrow.Kind
import arrow.core.Either
import arrow.core.OptionOf
import arrow.core.TryOf
import arrow.typeclasses.ApplicativeError

interface ApplicativeErrorSyntax<F, E> : ApplicativeError<F, E>, ApplicativeSyntax<F>, EitherSyntax<F, E> {

  suspend fun <A> E.raiseError(): A =
    ! run<ApplicativeError<F, E>, Kind<F, A>> { raiseError(this@raiseError) }

  suspend fun <A> raiseError(e: E, unit: Unit = Unit): A =
    ! run<ApplicativeError<F, E>, Kind<F, A>> { raiseError(e) }

  suspend fun <A> handleError(fa: suspend () -> A, recover: suspend (E) -> A): A =
    ! run<ApplicativeError<F, E>, Kind<F, A>> { fa.effect().handleErrorWith(recover.flatLiftM()) }

  suspend fun <A> OptionOf<A>.getOrRaiseError(f: () -> E): A =
    ! run<ApplicativeError<F, E>, Kind<F, A>> { this@getOrRaiseError.fromOption(f) }

  suspend fun <A, B> Either<B, A>.getOrRaiseError(f: (B) -> E): A =
    ! run<ApplicativeError<F, E>, Kind<F, A>> { this@getOrRaiseError.fromEither(f) }

  suspend fun <A> TryOf<A>.getOrRaiseError(f: (Throwable) -> E): A =
    ! run<ApplicativeError<F, E>, Kind<F, A>> { this@getOrRaiseError.fromTry(f) }

  suspend fun <A> attempt(fa: suspend () -> A): Either<E, A> =
    ! run<ApplicativeError<F, E>, Kind<F, Either<E, A>>> { fa.effect().attempt() }

}