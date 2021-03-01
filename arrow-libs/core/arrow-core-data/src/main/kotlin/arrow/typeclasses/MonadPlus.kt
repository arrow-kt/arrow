package arrow.typeclasses

import arrow.Kind
import arrow.KindDeprecation

@Deprecated(KindDeprecation)
/**
 *  MonadPlus is a typeclass that extends a Monad by supporting choice and failure.
 *  It is equal to [Alternative] in its api, but provides additional laws for how `flatMap` and `empty` interact.
 */
interface MonadPlus<F> : Monad<F>, Alternative<F> {

  fun <A> zeroM(): Kind<F, A> = empty()

  fun <A> Kind<F, A>.plusM(other: Kind<F, A>): Kind<F, A> = alt(other)
}
