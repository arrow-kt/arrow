package arrow.typeclasses

import arrow.Kind

interface MonadPlus<F> : Monad<F>, Alternative<F> {
  fun <A> zeroM(): Kind<F, A> = empty()

  fun <A> Kind<F, A>.plusM(other: Kind<F, A>): Kind<F, A> = alt(other)
}
