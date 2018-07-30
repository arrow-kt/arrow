package arrow.typeclasses

import arrow.Kind2
import arrow.core.identity

interface Bifunctor<F> {
    fun <A, B, C, D> Kind2<F, A, B>.bimap(fl: (A) -> C, fr: (B) -> D): Kind2<F, C, D>

    fun <A, B, C> Kind2<F, A, B>.mapLeft(f: (A) -> C): Kind2<F, C, B> = bimap(f, ::identity)
}
