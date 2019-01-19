package arrow.effects.typeclasses.suspended

import arrow.Kind
import arrow.core.Either
import arrow.core.Tuple2
import arrow.core.Tuple3
import arrow.typeclasses.ApplicativeError

interface EitherSyntax<F, E> : MonadSyntax<F>, ApplicativeError<F, E> {

  suspend fun <A, B> validate(
    a: Either<E, A>,
    b: Either<E, B>
  ): Either<E, Tuple2<A, B>> =
    arrow.core.extensions.either.applicative.tupled(a, b)

  suspend fun <A, B, C> validate(
    a: Either<E, A>,
    b: Either<E, B>,
    c: Either<E, C>
  ): Either<E, Tuple3<A, B, C>> =
    arrow.core.extensions.either.applicative.tupled(a, b, c)

  suspend fun <A, B, C> validate(
    a: Either<E, A>,
    b: Either<E, B>,
    c: Either<E, C>,
    d: Either<E, C>
  ): Either<E, Tuple3<A, B, C>> =
    arrow.core.extensions.either.applicative.tupled(a, b, c)

  suspend operator fun <A> Either<E, A>.component1(): A =
    fold<Kind<F, A>>(::raiseError, ::just).bind()

}