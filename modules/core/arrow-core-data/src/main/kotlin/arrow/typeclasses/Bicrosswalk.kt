package arrow.typeclasses

import arrow.Kind
import arrow.Kind2
import arrow.core.identity

interface Bicrosswalk<T> : Bifunctor<T>, Bifoldable<T> {
  fun <F, A, B, C, D> bicrosswalk(
    ALIGN: Align<F>,
    fa: (A) -> Kind<F, C>,
    fb: (B) -> Kind<F, D>,
    tab: Kind2<T, A, B>
  ): Kind<F, Kind2<T, C, D>> =
    bisequenceL(ALIGN, tab.bimap(fa, fb))

  fun <F, A, B> bisequenceL(
    ALIGN: Align<F>,
    tab: Kind2<T, Kind<F, A>, Kind<F, B>>
  ): Kind<F, Kind2<T, A, B>> =
    bicrosswalk(ALIGN, ::identity, ::identity, tab)
}
