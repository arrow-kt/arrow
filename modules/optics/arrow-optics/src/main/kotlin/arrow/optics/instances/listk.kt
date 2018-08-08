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
 * [Traversal] for [ListK] that has focus in each [A].
 *
 * @receiver [ListK.Companion] to make it statically available.
 * @return [Traversal] with source [ListK] and focus every [A] of the source.
 */
fun <A> ListK.Companion.traversal(): Traversal<ListK<A>, A> = object : Traversal<ListK<A>, A> {
  override fun <F> modifyF(FA: Applicative<F>, s: ListK<A>, f: (A) -> Kind<F, A>): Kind<F, ListK<A>> =
    s.traverse(FA, f)
}

/**
 * [Each] instance definition for [ListK].
 */
@extension
interface ListKEachInstance<A> : Each<ListK<A>, A> {
  override fun each(): Traversal<ListK<A>, A> =
    ListK.traversal()
}

/**
 * [FilterIndex] instance definition for [ListK].
 */
@extension
interface ListKFilterIndexInstance<A> : FilterIndex<ListK<A>, Int, A> {
  override fun filter(p: (Int) -> Boolean): Traversal<ListK<A>, A> = object : Traversal<ListK<A>, A> {
    override fun <F> modifyF(FA: Applicative<F>, s: ListK<A>, f: (A) -> Kind<F, A>): Kind<F, ListK<A>> = FA.run {
      s.mapIndexed { index, a -> a toT index }.k().traverse(FA, { (a, j) ->
        if (p(j)) f(a) else just(a)
      })
    }
  }
}

/**
 * [Index] instance definition for [ListK].
 */
@extension
interface ListKIndexInstance<A> : Index<ListK<A>, Int, A> {
  override fun index(i: Int): Optional<ListK<A>, A> = POptional(
    getOrModify = { it.getOrNull(i)?.right() ?: it.left() },
    set = { a -> { l -> l.mapIndexed { index: Int, aa: A -> if (index == i) a else aa }.k() } }
  )
}