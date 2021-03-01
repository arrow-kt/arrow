package arrow.typeclasses

import arrow.Kind
import arrow.KindDeprecation
import arrow.core.identity

@Deprecated(KindDeprecation)
interface Crosswalk<T> : Functor<T>, Foldable<T> {
  fun <F, A, B> crosswalk(ALIGN: Align<F>, a: Kind<T, A>, fa: (A) -> Kind<F, B>): Kind<F, Kind<T, B>> =
    sequenceL(ALIGN, a.map(fa))

  fun <F, A> sequenceL(ALIGN: Align<F>, tfa: Kind<T, Kind<F, A>>): Kind<F, Kind<T, A>> =
    crosswalk(ALIGN, tfa, ::identity)
}
