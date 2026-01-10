package arrow.optics.typeclasses

import arrow.core.NonEmptyList
import arrow.core.Predicate
import arrow.optics.Every
import arrow.optics.PLens
import arrow.optics.Traversal
import kotlin.jvm.JvmStatic

/**
 * [FilterIndex] provides a [Every] for a structure [S] with all its foci [A] whose index [I] satisfies a predicate.
 *
 * @param S source of [Every]
 * @param I index that uniquely identifies every focus of the [Every]
 * @param A focus that is supposed to be unique for a given pair [S] and [I]
 */
public fun interface FilterIndex<S, I, A> {

  /**
   * Filter the foci [A] of a [Every] with the predicate [p].
   */
  public fun filter(p: Predicate<I>): Traversal<S, A>

  public companion object {

    /**
     * [FilterIndex] instance definition for [List].
     */
    @JvmStatic
    public fun <A> list(): FilterIndex<List<A>, Int, A> =
      FilterIndex { p ->
        object : Traversal<List<A>, A> {
          override fun <R> foldMap(initial: R, combine: (R, R) -> R, source: List<A>, map: (A) -> R): R =
            source.foldIndexed(initial) { index, acc, a -> if (p(index)) combine(acc, map(a)) else acc }

          override fun modify(source: List<A>, map: (focus: A) -> A): List<A> =
            source.mapIndexed { index, a -> if (p(index)) map(a) else a }
        }
      }

    @JvmStatic
    public fun <K, V> map(): FilterIndex<Map<K, V>, K, V> =
      FilterIndex { p ->
        object : Traversal<Map<K, V>, V> {
          override fun <R> foldMap(initial: R, combine: (R, R) -> R, source: Map<K, V>, map: (V) -> R): R =
            source.entries.fold(initial) { acc, (k, v) ->
              if (p(k)) combine(acc, map(v)) else acc
            }

          override fun modify(source: Map<K, V>, map: (focus: V) -> V): Map<K, V> =
            source.mapValues { (k, v) -> if (p(k)) map(v) else v }
        }
      }

    /**
     * [FilterIndex] instance definition for [NonEmptyList].
     */
    @JvmStatic
    public fun <A> nonEmptyList(): FilterIndex<NonEmptyList<A>, Int, A> =
      FilterIndex { p ->
          object : Traversal<NonEmptyList<A>, A> {
              override fun <R> foldMap(initial: R, combine: (R, R) -> R, source: NonEmptyList<A>, map: (A) -> R): R =
                source.foldIndexed(initial) { index, acc, r ->
                  if (p(index)) combine(acc, map(r)) else acc
                }

              override fun modify(source: NonEmptyList<A>, map: (focus: A) -> A): NonEmptyList<A> =
                source.mapIndexed { index, a -> if (p(index)) map(a) else a }
          }
      }

    @JvmStatic
    public fun <A> sequence(): FilterIndex<Sequence<A>, Int, A> =
      FilterIndex { p ->
        object : Traversal<Sequence<A>, A> {
          override fun <R> foldMap(initial: R, combine: (R, R) -> R, source: Sequence<A>, map: (A) -> R): R =
            source.foldIndexed(initial) { index, acc, a ->
              if (p(index)) combine(acc, map(a)) else acc
            }

          override fun modify(source: Sequence<A>, map: (focus: A) -> A): Sequence<A> =
            source.mapIndexed { index, a -> if (p(index)) map(a) else a }
        }
      }

    /**
     * [FilterIndex] instance for [String].
     * It allows filtering of every [Char] in a [String] by its index's position.
     *
     * @receiver [FilterIndex.Companion] to make the instance statically available.
     * @return [FilterIndex] instance
     */
    @JvmStatic
    public fun string(): FilterIndex<String, Int, Char> =
      FilterIndex { p ->
        PLens.stringToList() compose list<Char>().filter(p)
      }
  }
}
