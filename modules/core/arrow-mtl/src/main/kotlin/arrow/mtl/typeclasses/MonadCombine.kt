package arrow.mtl.typeclasses

import arrow.Kind
import arrow.Kind2
import arrow.core.Tuple2
import arrow.typeclasses.Alternative
import arrow.typeclasses.Bifoldable
import arrow.typeclasses.Foldable

/**
 * ank_macro_hierarchy(arrow.mtl.typeclasses.MonadCombine)
 */
interface MonadCombine<F> : MonadFilter<F>, Alternative<F> {

  fun <G, A> Kind<F, Kind<G, A>>.unite(FG: Foldable<G>): Kind<F, A> = FG.run {
    flatMap { ga -> ga.foldLeft(empty<A>()) { acc, a -> acc.combineK(just(a)) } }
  }

  fun <G, A, B> Kind<F, Kind2<G, A, B>>.separate(BFG: Bifoldable<G>): Tuple2<Kind<F, A>, Kind<F, B>> = BFG.run {
    val asep = flatMap { gab -> run { gab.bifoldMap(algebra<A>(), { just(it) }, { empty() }) } }
    val bsep = flatMap { gab -> run { gab.bifoldMap(algebra<B>(), { empty() }, { just(it) }) } }
    return Tuple2(asep, bsep)
  }
}