package arrow.optics.extensions

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
 * [Traversal] for [SequenceK] that has focus in each [A].
 *
 * @receiver [SequenceK.Companion] to make it statically available.
 * @return [Traversal] with source [SequenceK] and focus in every [A] of the source.
 */
fun <A> SequenceK.Companion.traversal(): Traversal<SequenceK<A>, A> = object : Traversal<SequenceK<A>, A> {
  override fun <F> modifyF(FA: Applicative<F>, s: SequenceK<A>, f: (A) -> Kind<F, A>): Kind<F, SequenceK<A>> =
    s.traverse(FA, f)
}

/**
 * [Each] instance definition for [SequenceK].
 */
@extension
interface SequenceKEach<A> : Each<SequenceK<A>, A> {
  override fun each(): Traversal<SequenceK<A>, A> =
    SequenceK.traversal()
}

/**
 * [FilterIndex] instance definition for [SequenceK].
 */
@extension
interface SequenceKFilterIndex<A> : FilterIndex<SequenceK<A>, Int, A> {
  override fun filter(p: (Int) -> Boolean): Traversal<SequenceK<A>, A> = object : Traversal<SequenceK<A>, A> {
    override fun <F> modifyF(FA: Applicative<F>, s: SequenceK<A>, f: (A) -> Kind<F, A>): Kind<F, SequenceK<A>> = FA.run {
      s.mapIndexed { index, a -> a toT index }.k().traverse(FA) { (a, j) ->
        if (p(j)) f(a) else just(a)
      }
    }
  }
}

/**
 * [Index] instance definition for [SequenceK].
 */
@extension
interface SequenceKIndex<A> : Index<SequenceK<A>, Int, A> {
  override fun index(i: Int): Optional<SequenceK<A>, A> = POptional(
    getOrModify = { it.elementAtOrNull(i)?.right() ?: it.left() },
    set = { s, a -> s.mapIndexed { index, aa -> if (index == i) a else aa }.k() }
  )
}