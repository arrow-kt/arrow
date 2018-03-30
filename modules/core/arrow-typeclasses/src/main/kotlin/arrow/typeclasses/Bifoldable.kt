package arrow.typeclasses

import arrow.Kind2
import arrow.core.Eval

inline operator fun <F, A> Bifoldable<F>.invoke(ff: Bifoldable<F>.() -> A) =
        run(ff)

interface Bifoldable<F> {

    fun <A, B, C> Kind2<F, A, B>.bifoldLeft(c: C, f: (C, A) -> C, g: (C, B) -> C): C

    fun <A, B, C> Kind2<F, A, B>.bifoldRight(c: Eval<C>, f: (A, Eval<C>) -> Eval<C>, g: (B, Eval<C>) -> Eval<C>): Eval<C>

    fun <A, B, C> Kind2<F, A, B>.bifoldMap(MN: Monoid<C>, f: (A) -> C, g: (B) -> C) = MN.run {
        bifoldLeft(MN.empty(), { c, a -> c.combine(f(a)) }, { c, b -> c.combine(g(b)) })
    }
}
