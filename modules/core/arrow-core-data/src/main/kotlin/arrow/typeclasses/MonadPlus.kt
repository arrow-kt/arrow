package arrow.typeclasses

import arrow.Kind

interface MonadPlus<F> : Monad<F>, Alternative<F> {
  fun <A> mzero(): Kind<F, A> = empty()

  fun <A> Kind<F, A>.mplus(other: Kind<F, A>): Kind<F, A> = alt(other)
}
