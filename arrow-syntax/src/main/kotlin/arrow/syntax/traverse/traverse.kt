package arrow.syntax.traverse

import arrow.*

inline fun <reified F, reified G, A, B> HK<F, A>.traverse(
        FT: Traverse<F> = traverse(),
        GA: Applicative<G> = applicative(),
        noinline f: (A) -> HK<G, B>): HK<G, HK<F, B>> = FT.traverse(this, f, GA)

inline fun <reified F, reified G, A, B> HK<F, A>.flatTraverse(
        FT: Traverse<F> = traverse(),
        GA: Applicative<G> = applicative(),
        FM: Monad<F> = monad(), noinline f: (A) -> HK<G, HK<F, B>>): HK<G, HK<F, B>> = GA.map(FT.traverse(this, f, GA), { FM.flatten(it) })