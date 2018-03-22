package arrow.mtl.typeclasses

import arrow.Kind
import arrow.Kind2
import arrow.core.Tuple2
import arrow.typeclasses.Alternative
import arrow.typeclasses.Bifoldable
import arrow.typeclasses.Foldable

/**
 * The combination of a Monad with a MonoidK
 */
interface MonadCombine<F> : MonadFilter<F>, Alternative<F> {

    fun <G, A> unite(fga: Kind<F, Kind<G, A>>, FG: Foldable<G>): Kind<F, A> =
            flatMap(fga, { ga -> FG.foldLeft(ga, empty<A>(), { acc, a -> combineK(acc, pure(a)) }) })

    fun <G, A, B> separate(fgab: Kind<F, Kind2<G, A, B>>, BFG: Bifoldable<G>): Tuple2<Kind<F, A>, Kind<F, B>> {
        val asep = flatMap(fgab, { gab -> BFG.run { algebra<A>().bifoldMap(gab, { pure(it) }, { _ -> empty() }) } })
        val bsep = flatMap(fgab, { gab -> BFG.run { algebra<B>().bifoldMap(gab, { _ -> empty() }, { pure(it) }) } })
        return Tuple2(asep, bsep)
    }
}
