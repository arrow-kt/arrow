package kategory.typeclasses

import kategory.*

interface Bifoldable<in F> : Typeclass {

    fun <A, B, C> bifoldLeft(fab: HK2<F, A, B>, c: C, f: (C, A) -> C, g: (C, B) -> C): C

    fun <A, B, C> bifoldRight(fab: HK2<F, A, B>, c: Eval<C>, f: (A, Eval<C>) -> Eval<C>, g: (B, Eval<C>) -> Eval<C>): Eval<C>

    fun <A, B, C> bifoldMap(fab: HK2<F, A, B>, f: (A) -> C, g: (B) -> C, MC: Monoid<C>) =
            bifoldLeft(fab, MC.empty(), { c, a -> MC.combine(c, f(a)) }, { c, b -> MC.combine(c, g(b)) })
}

inline fun <F, A, B, reified C> Bifoldable<in F>.bifoldMap(fab: HK2<F, A, B>, noinline f: (A) -> C, noinline g: (B) -> C, MC: Monoid<C> = monoid()) =
        bifoldMap(fab, f, g, MC)

inline fun <reified F> bifoldable(): Bifoldable<F> = instance(InstanceParametrizedType(Bifoldable::class.java, listOf(F::class.java)))
