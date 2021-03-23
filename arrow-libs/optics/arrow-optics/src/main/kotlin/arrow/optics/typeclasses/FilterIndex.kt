package arrow.optics.typeclasses

import arrow.Kind
import arrow.core.Predicate
import arrow.core.Tuple2
import arrow.core.k
import arrow.core.toT
import arrow.optics.Iso
import arrow.optics.Traversal
import arrow.typeclasses.Applicative
import arrow.typeclasses.Traverse

/**
 * [FilterIndex] provides a [Traversal] for a structure [S] with all its foci [A] whose index [I] satisfies a predicate.
 *
 * @param S source of [Traversal]
 * @param I index that uniquely identifies every focus of the [Traversal]
 * @param A focus that is supposed to be unique for a given pair [S] and [I]
 */
fun interface FilterIndex<S, I, A> {

  /**
   * Filter the foci [A] of a [Traversal] with the predicate [p].
   */
  fun filter(p: Predicate<I>): Traversal<S, A>

  companion object {

    /**
     * Lift an instance of [FilterIndex] using an [Iso]
     */
    fun <S, A, I, B> fromIso(FI: FilterIndex<A, I, B>, iso: Iso<S, A>): FilterIndex<S, I, B> =
      FilterIndex { p -> iso compose FI.filter(p) }

    /**
     * Create an instance of [FilterIndex] from a [Traverse] and a function `Kind<S, A>) -> Kind<S, Tuple2<A, Int>>`
     */
    fun <S, A> fromTraverse(
      zipWithIndex: (Kind<S, A>) -> Kind<S, Tuple2<A, Int>>,
      traverse: Traverse<S>
    ): FilterIndex<Kind<S, A>, Int, A> =
      FilterIndex { p ->
        object : Traversal<Kind<S, A>, A> {
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

    /**
     * [FilterIndex] instance definition for [List].
     */
    @JvmStatic
    fun <A> list(): FilterIndex<List<A>, Int, A> =
      FilterIndex { p ->
        object : Traversal<List<A>, A> {
          override fun <F> modifyF(FA: Applicative<F>, s: List<A>, f: (A) -> Kind<F, A>): Kind<F, List<A>> =
            s.mapIndexed { index, a -> a toT index }.k().traverse(FA) { (a, j) ->
              if (p(j)) f(a) else FA.just(a)
            }
        }
      }

    @JvmStatic
    fun <K, V> map(): FilterIndex<Map<K, V>, K, V> = FilterIndex { p ->
      object : Traversal<Map<K, V>, V> {
        override fun <F> modifyF(FA: Applicative<F>, s: Map<K, V>, f: (V) -> Kind<F, V>): Kind<F, Map<K, V>> = FA.run {
          s.toList().k().traverse(FA) { (k, v) ->
            (if (p(k)) f(v) else just(v)).map {
              k to it
            }
          }.map { it.toMap() }
        }
      }
    }
  }
}
