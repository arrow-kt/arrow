package arrow.optics.extensions

import arrow.Kind
import arrow.core.left
import arrow.core.right
import arrow.core.toT
import arrow.core.NonEmptyList
import arrow.optics.Optional
import arrow.optics.POptional
import arrow.optics.Traversal
import arrow.optics.typeclasses.Each
import arrow.optics.typeclasses.FilterIndex
import arrow.optics.typeclasses.Index
import arrow.typeclasses.Applicative

/**
 * [Traversal] for [NonEmptyList] that has focus in each [A].
 *
 * @receiver [NonEmptyList.Companion] to make it statically available.
 * @return [Traversal] with source [NonEmptyList] and focus every [A] of the source.
 */
@Deprecated(
  "Typeclass instance have been moved to the companion object of the typeclass",
  ReplaceWith(
    "Traversal.nonEmptyList<A>()",
    "arrow.optics.Traversal", "arrow.optics.nonEmptyList"
  ),
  DeprecationLevel.WARNING
)
fun <A> NonEmptyList.Companion.traversal(): Traversal<NonEmptyList<A>, A> = object : Traversal<NonEmptyList<A>, A> {
  override fun <F> modifyF(FA: Applicative<F>, s: NonEmptyList<A>, f: (A) -> Kind<F, A>): Kind<F, NonEmptyList<A>> =
    s.traverse(FA, f)
}

/**
 * [Each] instance definition for [NonEmptyList].
 */
@Deprecated(
  "Each is being deprecated. Use Traversal directly instead.",
  ReplaceWith(
    "Traversal.nonEmptyList<A>()",
    "arrow.optics.Traversal", "arrow.optics.nonEmptyList"
  ),
  DeprecationLevel.WARNING
)
interface NonEmptyListEach<A> : Each<NonEmptyList<A>, A> {
  override fun each(): Traversal<NonEmptyList<A>, A> =
    NonEmptyList.traversal()
}

/**
 * [FilterIndex] instance definition for [NonEmptyList].
 */
@Deprecated(
  "Typeclass interface implementation will not be exposed directly anymore",
  ReplaceWith(
    "FilterIndex.nonEmptyList<A>()",
    "arrow.optics.nonEmptyList", "arrow.optics.typeclasses.FilterIndex"
  ),
  DeprecationLevel.WARNING
)
interface NonEmptyListFilterIndex<A> : FilterIndex<NonEmptyList<A>, Int, A> {
  override fun filter(p: (Int) -> Boolean): Traversal<NonEmptyList<A>, A> = object : Traversal<NonEmptyList<A>, A> {
    override fun <F> modifyF(FA: Applicative<F>, s: NonEmptyList<A>, f: (A) -> Kind<F, A>): Kind<F, NonEmptyList<A>> =
      s.all.mapIndexed { index, a -> a toT index }
        .let(NonEmptyList.Companion::fromListUnsafe)
        .traverse(FA) { (a, j) -> if (p(j)) f(a) else FA.just(a) }
  }
}

/**
 * [Index] instance definition for [NonEmptyList].
 */
@Deprecated(
  "Typeclass interface implementation will not be exposed directly anymore",
  ReplaceWith(
    "Index.nonEmptyList<A>()",
    "arrow.optics.nonEmptyList", "arrow.optics.typeclasses.Index"
  ),
  DeprecationLevel.WARNING
)
interface NonEmptyListIndex<A> : Index<NonEmptyList<A>, Int, A> {
  override fun index(i: Int): Optional<NonEmptyList<A>, A> = POptional(
    getOrModify = { l -> l.all.getOrNull(i)?.right() ?: l.left() },
    set = { l, a ->
      NonEmptyList.fromListUnsafe(
        l.all.mapIndexed { index: Int, aa: A -> if (index == i) a else aa }
      )
    }
  )
}
