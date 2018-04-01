package arrow.optics.instances

import arrow.core.Left
import arrow.core.Right
import arrow.core.toT
import arrow.data.*
import arrow.instance
import arrow.optics.Optional
import arrow.optics.POptional
import arrow.optics.Traversal
import arrow.optics.typeclasses.FilterIndex
import arrow.optics.typeclasses.Index

@instance(SequenceK::class)
interface SequenceKFilterIndexInstance<A> : FilterIndex<SequenceK<A>, Int, A> {
  override fun filter(p: (Int) -> Boolean): Traversal<SequenceK<A>, A> =
  // FIXME(paco) we're not scala, so we need to safely downcast
    FilterIndex.fromTraverse<ForSequenceK, A>({ aas ->
      aas.fix().mapIndexed { index, a -> a toT index }.k()
    }, SequenceK.traverse()).filter(p) as Traversal<SequenceK<A>, A>
}

@instance(SequenceK::class)
interface SequenceKIndexInstance<A> : Index<SequenceK<A>, Int, A> {
  override fun index(i: Int): Optional<SequenceK<A>, A> = POptional(
    getOrModify = { it.fix().sequence.elementAtOrNull(i)?.let(::Right) ?: it.fix().let(::Left) },
    set = { a -> { it.fix().mapIndexed { index, aa -> if (index == i) a else aa }.k() } }
  )
}