package arrow.effects.typeclasses.suspended

import arrow.Kind
import arrow.core.Either
import arrow.core.Tuple2
import arrow.core.Tuple3
import arrow.typeclasses.ApplicativeError

interface EitherSyntax<F, E> : MonadSyntax<F>, ApplicativeError<F, E> {

  suspend operator fun <A> Either<E, A>.component1(): A =
    fold<Kind<F, A>>(::raiseError, ::just).bind()

}