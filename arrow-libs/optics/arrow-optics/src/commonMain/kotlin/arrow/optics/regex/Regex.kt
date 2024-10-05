package arrow.optics.regex

import arrow.optics.Iso
import arrow.optics.Traversal
import arrow.optics.PTraversal

/**
 * [Traversal] that aggregates the elements of two other traversals.
 *
 * For example, this may aggregate the values coming from several fields of the same type.
 */
public infix fun <T, A, B> PTraversal<T, T, A, B>.and(other: PTraversal<T, T, A, B>): PTraversal<T, T, A, B> =
  object : PTraversal<T, T, A, B> {
    override fun <R> foldMap(initial: R, combine: (R, R) -> R, source: T, map: (focus: A) -> R): R {
      val meFolded = this@and.foldMap(initial, combine, source, map)
      val otherFolded = other.foldMap(initial, combine, source, map)
      return combine(meFolded, otherFolded)
    }

    override fun modify(source: T, map: (focus: A) -> B): T =
      other.modify(this@and.modify(source, map), map)
  }

/**
 * [Traversal] that aggregates the elements of two other traversals.
 *
 * For example, this may aggregate the values coming from several fields of the same type.
 */
public operator fun <T, A, B> PTraversal<T, T, A, B>.times(other: PTraversal<T, T, A, B>): PTraversal<T, T, A, B> =
  this and other

/**
 * Aggregates the elements by "going into" the desired traversal repeatedly.
 * This traversal is especially useful to inspect values which contain fields of
 * the same type within them.
 *
 * **Important**: [onceOrMore] may operate over the same value more than once,
 * if there is more than one path arriving to it.
 */
public fun <A> onceOrMore(traversal: Traversal<A, A>): Traversal<A, A> =
  traversal composeLazy { zeroOrMore(traversal) }

/**
 * Aggregates the elements by "going into" the desired traversal repeatedly,
 * including the starting value itself.
 * This traversal is especially useful to inspect values which contain fields of
 * the same type within them.
 *
 * **Important**: [zeroOrMore] may operate over the same value more than once,
 * if there is more than one path arriving to it.
 */
public fun <A> zeroOrMore(traversal: Traversal<A, A>): Traversal<A, A> =
  Iso.id<A>() and onceOrMore(traversal)
