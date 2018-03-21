package arrow.typeclasses

import arrow.Kind2
import arrow.core.Eval

interface Bifoldable<F> {

    fun <A, B, C> bifoldLeft(fab: Kind2<F, A, B>, c: C, f: (C, A) -> C, g: (C, B) -> C): C

    fun <A, B, C> bifoldRight(fab: Kind2<F, A, B>, c: Eval<C>, f: (A, Eval<C>) -> Eval<C>, g: (B, Eval<C>) -> Eval<C>): Eval<C>

    fun <A, B, C> Monoid<C>.bifoldMap(fab: Kind2<F, A, B>, f: (A) -> C, g: (B) -> C) =
            bifoldLeft(fab, empty(), { c, a -> c.combine(f(a)) }, { c, b -> c.combine(g(b)) })
}
