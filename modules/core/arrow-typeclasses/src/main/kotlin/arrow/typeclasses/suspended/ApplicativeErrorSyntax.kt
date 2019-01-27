package arrow.typeclasses.suspended

import arrow.Kind
import arrow.core.Either
import arrow.core.OptionOf
import arrow.core.TryOf
import arrow.typeclasses.ApplicativeError

interface ApplicativeErrorSyntax<F, E> : ApplicativeError<F, E>, ApplicativeSyntax<F>, EitherSyntax<F, E> {

  suspend fun <A> E.raiseError(): A =
    run<ApplicativeError<F, E>, Kind<F, A>> { raiseError(this@raiseError) }.bind()

  suspend fun <A> raiseError(e: E, unit: Unit = Unit): A =
    run<ApplicativeError<F, E>, Kind<F, A>> { raiseError(e) }.bind()

  suspend fun <A> handleError(fa: suspend () -> A, recover: suspend (E) -> A): A =
    run<ApplicativeError<F, E>, Kind<F, A>> { fa.liftM().handleErrorWith(recover.flatLiftM()) }.bind()

  suspend fun <A> OptionOf<A>.getOrRaiseError(f: () -> E): A =
    run<ApplicativeError<F, E>, Kind<F, A>> { this@getOrRaiseError.fromOption(f) }.bind()

  suspend fun <A, B> Either<B, A>.getOrRaiseError(f: (B) -> E): A =
    run<ApplicativeError<F, E>, Kind<F, A>> { this@getOrRaiseError.fromEither(f) }.bind()

  suspend fun <A> TryOf<A>.getOrRaiseError(f: (Throwable) -> E): A =
    run<ApplicativeError<F, E>, Kind<F, A>> { this@getOrRaiseError.fromTry(f) }.bind()

  suspend fun <A> attempt(fa: suspend () -> A): Either<E, A> =
    run<ApplicativeError<F, E>, Kind<F, Either<E, A>>> { fa.liftM().attempt() }.bind()

}