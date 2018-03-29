package arrow.mtl.typeclasses

import arrow.Kind
import arrow.Kind2
import arrow.core.Tuple2
import arrow.typeclasses.Alternative
import arrow.typeclasses.Bifoldable
import arrow.typeclasses.Foldable

inline operator fun <F, A> MonadCombine<F>.invoke(ff: MonadCombine<F>.() -> A) =
        run(ff)

/**
 * The combination of a Monad with a MonoidK
 */
interface MonadCombine<F> : MonadFilter<F>, Alternative<F> {

    fun <G, A> Kind<F, Kind<G, A>>.unite(FG: Foldable<G>): Kind<F, A> = FG.run {
        flatMap({ ga -> ga.foldLeft(empty<A>(), { acc, a -> acc.combineK(pure(a)) }) })
    }

    fun <G, A, B> Kind<F, Kind2<G, A, B>>.separate(BFG: Bifoldable<G>): Tuple2<Kind<F, A>, Kind<F, B>> = BFG.run {
        val asep = this@separate.flatMap({ gab -> run { gab.bifoldMap(algebra<A>(), { pure(it) }, { _ -> empty() }) } })
        val bsep = this@separate.flatMap({ gab -> run { gab.bifoldMap(algebra<B>(), { _ -> empty() }, { pure(it) }) } })
        return Tuple2(asep, bsep)
    }
}
