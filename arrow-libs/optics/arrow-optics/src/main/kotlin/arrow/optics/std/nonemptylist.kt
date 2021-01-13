package arrow.optics

import arrow.Kind
import arrow.core.NonEmptyList
import arrow.core.left
import arrow.core.right
import arrow.core.toT
import arrow.optics.typeclasses.FilterIndex
import arrow.optics.typeclasses.Index
import arrow.typeclasses.Applicative

/**
 * [Lens] to operate on the head of a [NonEmptyList]
 */
@Deprecated(
  "Use the nonEmptyListHead function exposed in the Lens' companion object",
  ReplaceWith(
    "Lens.nonEmptyListHead<A>()",
    "arrow.optics.Lens", "arrow.optics.nonEmptyListHead"
  ),
  DeprecationLevel.WARNING
)
fun <A> NonEmptyList.Companion.head(): Lens<NonEmptyList<A>, A> = Lens(
  get = NonEmptyList<A>::head,
  set = { nel, newHead -> NonEmptyList(newHead, nel.tail) }
)

/**
 * [Lens] to operate on the head of a [NonEmptyList]
 */
fun <A> PLens.Companion.nonEmptyListHead(): Lens<NonEmptyList<A>, A> = Lens(
  get = NonEmptyList<A>::head,
  set = { nel, newHead -> NonEmptyList(newHead, nel.tail) }
)

/**
 * [Lens] to operate on the tail of a [NonEmptyList]
 */
@Deprecated(
  "Use the nonEmptyListTail function exposed in the Lens' companion object",
  ReplaceWith(
    "Lens.nonEmptyListTail<A>()",
    "arrow.optics.Lens", "arrow.optics.nonEmptyListTail"
  ),
  DeprecationLevel.WARNING
)
fun <A> NonEmptyList.Companion.tail(): Lens<NonEmptyList<A>, List<A>> = Lens(
  get = NonEmptyList<A>::tail,
  set = { nel, newTail -> NonEmptyList(nel.head, newTail) }
)

/**
 * [Lens] to operate on the tail of a [NonEmptyList]
 */
fun <A> PLens.Companion.nonEmptyListTail(): Lens<NonEmptyList<A>, List<A>> = Lens(
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
  object : Traversal<NonEmptyList<A>, A> {
    override fun <F> modifyF(FA: Applicative<F>, s: NonEmptyList<A>, f: (A) -> Kind<F, A>): Kind<F, NonEmptyList<A>> =
      s.traverse(FA, f)
  }

/**
 * [FilterIndex] instance definition for [NonEmptyList].
 */
fun <A> FilterIndex.Companion.nonEmptyList(): FilterIndex<NonEmptyList<A>, Int, A> = FilterIndex { p ->
  object : Traversal<NonEmptyList<A>, A> {
    override fun <F> modifyF(FA: Applicative<F>, s: NonEmptyList<A>, f: (A) -> Kind<F, A>): Kind<F, NonEmptyList<A>> =
      s.all.mapIndexed { index, a -> a toT index }
        .let(NonEmptyList.Companion::fromListUnsafe)
        .traverse(FA) { (a, j) -> if (p(j)) f(a) else FA.just(a) }
  }
}

/**
 * [Index] instance definition for [NonEmptyList].
 */
fun <A> Index.Companion.nonEmptyList(): Index<NonEmptyList<A>, Int, A> = Index { i ->
  POptional(
    getOrModify = { l -> l.all.getOrNull(i)?.right() ?: l.left() },
    set = { l, a ->
      NonEmptyList.fromListUnsafe(
        l.all.mapIndexed { index: Int, aa: A -> if (index == i) a else aa }
      )
    }
  )
}
