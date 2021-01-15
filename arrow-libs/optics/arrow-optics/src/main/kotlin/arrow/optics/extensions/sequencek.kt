package arrow.optics.extensions

import arrow.Kind
import arrow.core.SequenceK
import arrow.core.k
import arrow.core.left
import arrow.core.right
import arrow.core.toT
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
@Deprecated(
  "SequenceK is being deprecated. Use the instance for Sequence from the companion object of the typeclass.",
  ReplaceWith(
    "Traversal.sequence<A>()",
    "arrow.optics.Traversal", "arrow.optics.sequence"),
  DeprecationLevel.WARNING
)
fun <A> SequenceK.Companion.traversal(): Traversal<SequenceK<A>, A> = object : Traversal<SequenceK<A>, A> {
  override fun <F> modifyF(FA: Applicative<F>, s: SequenceK<A>, f: (A) -> Kind<F, A>): Kind<F, SequenceK<A>> =
    s.traverse(FA, f)
}

/**
 * [Each] instance definition for [SequenceK].
 */
@Deprecated(
  "Each is being deprecated. Use the instance for List from Traversal's companion object instead.",
  ReplaceWith(
    "Traversal.sequence<A>()",
    "arrow.optics.Traversal", "arrow.optics.sequence"),
  DeprecationLevel.WARNING)
interface SequenceKEach<A> : Each<SequenceK<A>, A> {
  override fun each(): Traversal<SequenceK<A>, A> =
    SequenceK.traversal()
}

/**
 * [FilterIndex] instance definition for [SequenceK].
 */
@Deprecated(
  "Typeclass interface implementation will not be exposed directly anymore.",
  ReplaceWith(
    "FilterIndex.sequence<A>()",
    "arrow.optics.map", "arrow.optics.typeclasses.FilterIndex"
  ),
  DeprecationLevel.WARNING
)
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
@Deprecated(
  "Typeclass interface implementation will not be exposed directly anymore.",
  ReplaceWith(
    "Index.sequence<A>()",
    "arrow.optics.map", "arrow.optics.typeclasses.Index"
  ),
  DeprecationLevel.WARNING
)
interface SequenceKIndex<A> : Index<SequenceK<A>, Int, A> {
  override fun index(i: Int): Optional<SequenceK<A>, A> = POptional(
    getOrModify = { it.elementAtOrNull(i)?.right() ?: it.left() },
    set = { s, a -> s.mapIndexed { index, aa -> if (index == i) a else aa }.k() }
  )
}
