package arrow.typeclasses

import arrow.Kind
import arrow.Kind2
import arrow.core.Tuple2

/**
 * ank_macro_hierarchy(arrow.typeclasses.MonadCombine)
 */
interface MonadCombine<F> : MonadFilter<F>, Alternative<F> {

  /**
   * Fold over the inner structure to combine all of the values with our combineK method inherited from MonoidK.
   * The result is for us to accumulate all of the "interesting" values of the inner G, so if G is Option,
   * we collect all the Some values, if G is Either, we collect all the Right values, etc.
   *
   * @receiver two nested contexts, being F a MonadCombine and G a Foldable.
   * @param FG Foldable instance for G.
   * @return the accumulation of values of the inner context G.
   */
  fun <G, A> Kind<F, Kind<G, A>>.unite(FG: Foldable<G>): Kind<F, A> = FG.run {
    flatMap { ga -> ga.foldLeft(empty<A>()) { acc, a -> acc.combineK(just(a)) } }
  }

  /**
   * Separate the inner foldable values into the "lefts" and "rights".
   *
   * @receiver two nested contexts, being F a MonadCombine and G a Bifoldable
   * @param BGF Bifoldable instance for G
   * @return a tuple containing one F context with "left" values of the Bifoldable and another F with its "right" values.
   */
  fun <G, A, B> Kind<F, Kind2<G, A, B>>.separate(BFG: Bifoldable<G>): Tuple2<Kind<F, A>, Kind<F, B>> = BFG.run {
    val asep = flatMap { gab -> run { gab.bifoldMap(algebra<A>(), { just(it) }, { empty() }) } }
    val bsep = flatMap { gab -> run { gab.bifoldMap(algebra<B>(), { empty() }, { just(it) }) } }
    return Tuple2(asep, bsep)
  }
}
