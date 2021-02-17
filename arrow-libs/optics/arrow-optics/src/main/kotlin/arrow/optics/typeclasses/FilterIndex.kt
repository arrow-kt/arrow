package arrow.optics.typeclasses

import arrow.core.Predicate
import arrow.optics.Every
import arrow.optics.Iso

/**
 * [FilterIndex] provides a [Every] for a structure [S] with all its foci [A] whose index [I] satisfies a predicate.
 *
 * @param S source of [Every]
 * @param I index that uniquely identifies every focus of the [Every]
 * @param A focus that is supposed to be unique for a given pair [S] and [I]
 */
fun interface FilterIndex<S, I, A> {

  /**
   * Filter the foci [A] of a [Every] with the predicate [p].
   */
  fun filter(p: Predicate<I>): Every<S, A>

  companion object {

    /**
     * Lift an instance of [FilterIndex] using an [Iso]
     */
    fun <S, A, I, B> fromIso(FI: FilterIndex<A, I, B>, iso: Iso<S, A>): FilterIndex<S, I, B> =
      FilterIndex { p -> iso compose FI.filter(p) }
  }
}
