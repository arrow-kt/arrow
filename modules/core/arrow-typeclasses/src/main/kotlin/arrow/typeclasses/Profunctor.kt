package arrow.typeclasses

import arrow.Kind2
import arrow.core.identity

/**
 * ank_macro_hierarchy(arrow.typeclasses.Profunctor)
 */
interface Profunctor<F> {
    fun <A, B, C, D> Kind2<F, A, B>.dimap(fl: (C) -> A, fr: (B) -> D): Kind2<F, C, D>

    fun <A, B, C> Kind2<F, A, B>.lmap(f: (C) -> A): Kind2<F, C, B> = dimap(f, ::identity)

    fun <A, B, D> Kind2<F, A, B>.rmap(f: (B) -> D): Kind2<F, A, D> = dimap(::identity, f)
}
