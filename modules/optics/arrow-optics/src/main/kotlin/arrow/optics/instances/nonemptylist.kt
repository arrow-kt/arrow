package arrow.optics.instances

import arrow.Kind
import arrow.core.*
import arrow.data.*
import arrow.extension
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
fun <A> NonEmptyList.Companion.traversal(): Traversal<NonEmptyList<A>, A> = object : Traversal<NonEmptyList<A>, A> {
  override fun <F> modifyF(FA: Applicative<F>, s: NonEmptyList<A>, f: (A) -> Kind<F, A>): Kind<F, NonEmptyList<A>> =
    s.traverse(FA, f)
}

/**
 * [Each] instance definition for [NonEmptyList].
 */
@extension
interface NonEmptyListEachInstance<A> : Each<NonEmptyList<A>, A> {
  override fun each(): Traversal<NonEmptyList<A>, A> =
    NonEmptyList.traversal()
}

/**
 * [FilterIndex] instance definition for [NonEmptyList].
 */
@extension
interface NonEmptyListFilterIndexInstance<A> : FilterIndex<NonEmptyList<A>, Int, A> {
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
@extension
interface NonEmptyListIndexInstance<A> : Index<NonEmptyList<A>, Int, A> {
  override fun index(i: Int): Optional<NonEmptyList<A>, A> = POptional(
    getOrModify = { l -> l.all.getOrNull(i)?.right() ?: l.left() },
    set = { l, a ->
      NonEmptyList.fromListUnsafe(
        l.all.mapIndexed { index: Int, aa: A -> if (index == i) a else aa }
      )
    }
  )
}