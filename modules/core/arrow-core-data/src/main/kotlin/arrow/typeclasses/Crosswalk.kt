package arrow.typeclasses

import arrow.Kind
import arrow.core.identity

interface Crosswalk<T> : Functor<T>, Foldable<T> {
  fun <F, A, B> crosswalk(ALIGN: Align<F>, fa: (A) -> Kind<F, B>, a: Kind<T, A>): Kind<F, Kind<T, B>> =
    sequenceL(ALIGN, a.map(fa))

  fun <F, A> sequenceL(ALIGN: Align<F>, tfa: Kind<T, Kind<F, A>>): Kind<F, Kind<T, A>> =
    crosswalk(ALIGN, ::identity, tfa)
}
