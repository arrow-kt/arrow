package arrow.syntax.traverse

import arrow.*
import arrow.typeclasses.*

inline fun <reified F, reified G, A, B> Kind<F, A>.traverse(
        FT: Traverse<F> = traverse(),
        GA: Applicative<G> = applicative(),
        noinline f: (A) -> Kind<G, B>): Kind<G, Kind<F, B>> = FT.traverse(this, f, GA)

inline fun <reified F, reified G, A, B> Kind<F, A>.flatTraverse(
        FT: Traverse<F> = traverse(),
        GA: Applicative<G> = applicative(),
        FM: Monad<F> = monad(), noinline f: (A) -> Kind<G, Kind<F, B>>): Kind<G, Kind<F, B>> = GA.map(FT.traverse(this, f, GA), { FM.flatten(it) })