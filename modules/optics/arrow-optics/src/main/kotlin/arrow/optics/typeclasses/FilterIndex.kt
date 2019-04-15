package arrow.optics.typeclasses

import arrow.Kind
import arrow.core.Predicate
import arrow.core.Tuple2
import arrow.optics.Iso
import arrow.optics.Traversal
import arrow.typeclasses.Applicative
import arrow.typeclasses.Traverse

/**
 * ank_macro_hierarchy(arrow.optics.typeclasses.FilterIndex)
 *
 * [FilterIndex] provides a [Traversal] for a structure [S] with all its foci [A] whose index [I] satisfies a predicate.
 *
 * @param S source of [Traversal]
 * @param I index that uniquely identifies every focus of the [Traversal]
 * @param A focus that is supposed to be unique for a given pair [S] and [I]
 */
interface FilterIndex<S, I, A> {

  /**
   * Filter the foci [A] of a [Traversal] with the predicate [p].
   */
  fun filter(p: Predicate<I>): Traversal<S, A>

  companion object {

    /**
     * Lift an instance of [FilterIndex] using an [Iso]
     */
    fun <S, A, I, B> fromIso(FI: FilterIndex<A, I, B>, iso: Iso<S, A>): FilterIndex<S, I, B> = object : FilterIndex<S, I, B> {
      override fun filter(p: Predicate<I>): Traversal<S, B> =
        iso compose FI.filter(p)
    }

    /**
     * Create an instance of [FilterIndex] from a [Traverse] and a function `Kind<S, A>) -> Kind<S, Tuple2<A, Int>>`
     */
    fun <S, A> fromTraverse(zipWithIndex: (Kind<S, A>) -> Kind<S, Tuple2<A, Int>>, traverse: Traverse<S>): FilterIndex<Kind<S, A>, Int, A> = object : FilterIndex<Kind<S, A>, Int, A> {
      override fun filter(p: Predicate<Int>): Traversal<Kind<S, A>, A> = object : Traversal<Kind<S, A>, A> {
        override fun <F> modifyF(FA: Applicative<F>, s: Kind<S, A>, f: (A) -> Kind<F, A>): Kind<F, Kind<S, A>> =
          traverse.run {
            FA.run {
              zipWithIndex(s).traverse(this) { (a, j) ->
                if (p(j)) f(a) else just(a)
              }
            }
          }
      }
    }
  }
}
