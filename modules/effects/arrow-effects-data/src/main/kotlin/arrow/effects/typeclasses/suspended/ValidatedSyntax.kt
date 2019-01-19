package arrow.effects.typeclasses.suspended

import arrow.core.Tuple2
import arrow.core.Tuple3
import arrow.core.Tuple4
import arrow.data.Nel
import arrow.data.Validated
import arrow.data.ValidatedNel
import arrow.data.extensions.nonemptylist.semigroup.semigroup
import arrow.typeclasses.ApplicativeError

interface ValidatedSyntax<F, E> : MonadSyntax<F>, ApplicativeError<F, E> {

  suspend fun <A, B> validate(
    a: ValidatedNel<E, A>,
    b: ValidatedNel<E, B>
  ): ValidatedNel<E, Tuple2<A, B>> =
    arrow.data.extensions.validated.applicative.tupled(Nel.semigroup(), a, b)

  suspend fun <A, B, C> validate(
    a: Validated<Nel<E>, A>,
    b: Validated<Nel<E>, B>,
    c: Validated<Nel<E>, C>
  ): ValidatedNel<E, Tuple3<A, B, C>> =
    arrow.data.extensions.validated.applicative.tupled(Nel.semigroup(), a, b, c)

  suspend fun <A, B, C, D> validate(
    a: Validated<Nel<E>, A>,
    b: Validated<Nel<E>, B>,
    c: Validated<Nel<E>, C>,
    d: Validated<Nel<E>, D>
  ): ValidatedNel<E, Tuple4<A, B, C, D>> =
    arrow.data.extensions.validated.applicative.tupled(Nel.semigroup(), a, b, c, d)

  fun <A> ValidatedNel<E, A>.errors(): List<E> =
    fold({ it.all }, { emptyList() })

}