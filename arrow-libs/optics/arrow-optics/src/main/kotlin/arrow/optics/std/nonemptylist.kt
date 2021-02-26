package arrow.optics

import arrow.core.NonEmptyList
import arrow.core.left
import arrow.core.right
import arrow.optics.typeclasses.FilterIndex
import arrow.optics.typeclasses.Index
import arrow.typeclasses.Monoid

/**
 * [Lens] to operate on the head of a [NonEmptyList]
 */
fun <A> PLens.Companion.nonEmptyListHead(): Lens<NonEmptyList<A>, A> =
  Lens(
    get = NonEmptyList<A>::head,
    set = { nel, newHead -> NonEmptyList(newHead, nel.tail) }
  )

/**
 * [Lens] to operate on the tail of a [NonEmptyList]
 */
fun <A> PLens.Companion.nonEmptyListTail(): Lens<NonEmptyList<A>, List<A>> =
  Lens(
    get = NonEmptyList<A>::tail,
    set = { nel, newTail -> NonEmptyList(nel.head, newTail) }
  )

/**
 * [Traversal] for [NonEmptyList] that has focus in each [A].
 *
 * @receiver [PTraversal.Companion] to make it statically available.
 * @return [Traversal] with source [NonEmptyList] and focus every [A] of the source.
 */
fun <A> PTraversal.Companion.nonEmptyList(): Traversal<NonEmptyList<A>, A> =
  Every.nonEmptyList()

fun <A> Fold.Companion.nonEmptyList(): Fold<NonEmptyList<A>, A> =
  Every.nonEmptyList()

fun <A> PEvery.Companion.nonEmptyList(): Every<NonEmptyList<A>, A> =
  object : Every<NonEmptyList<A>, A> {
    override fun <R> foldMap(M: Monoid<R>, source: NonEmptyList<A>, map: (A) -> R): R =
      M.run { source.fold(empty()) { acc, r -> acc.combine(map(r)) } }

    override fun modify(source: NonEmptyList<A>, map: (focus: A) -> A): NonEmptyList<A> =
      source.map(map)
  }

/**
 * [FilterIndex] instance definition for [NonEmptyList].
 */
fun <A> FilterIndex.Companion.nonEmptyList(): FilterIndex<NonEmptyList<A>, Int, A> =
  FilterIndex { p ->
    object : Every<NonEmptyList<A>, A> {
      override fun <R> foldMap(M: Monoid<R>, source: NonEmptyList<A>, map: (A) -> R): R = M.run {
        source.foldIndexed(empty()) { index, acc, r ->
          if (p(index)) acc.combine(map(r)) else acc
        }
      }

      override fun modify(source: NonEmptyList<A>, map: (focus: A) -> A): NonEmptyList<A> =
        NonEmptyList.fromListUnsafe(source.mapIndexed { index, a -> if (p(index)) map(a) else a })
    }
  }

/**
 * [Index] instance definition for [NonEmptyList].
 */
fun <A> Index.Companion.nonEmptyList(): Index<NonEmptyList<A>, Int, A> =
  Index { i ->
    POptional(
      getOrModify = { l -> l.all.getOrNull(i)?.right() ?: l.left() },
      set = { l, a ->
        NonEmptyList.fromListUnsafe(
          l.all.mapIndexed { index: Int, aa: A -> if (index == i) a else aa }
        )
      }
    )
  }
