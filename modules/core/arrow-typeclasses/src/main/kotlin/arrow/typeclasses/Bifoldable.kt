package arrow.typeclasses

import arrow.Kind2
import arrow.core.Eval

interface Bifoldable<F> {

    fun <A, B, C> bifoldLeft(fab: Kind2<F, A, B>, c: C, f: (C, A) -> C, g: (C, B) -> C): C

    fun <A, B, C> bifoldRight(fab: Kind2<F, A, B>, c: Eval<C>, f: (A, Eval<C>) -> Eval<C>, g: (B, Eval<C>) -> Eval<C>): Eval<C>

    fun <A, B, C> Kind2<F, A, B>.bifoldMap(MN: Monoid<C>, f: (A) -> C, g: (B) -> C) = MN.run {
        bifoldLeft(this@bifoldMap, MN.empty(), { c, a -> c.combine(f(a)) }, { c, b -> c.combine(g(b)) })
    }
}
