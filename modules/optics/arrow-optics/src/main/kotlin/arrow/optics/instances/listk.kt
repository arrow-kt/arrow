package arrow.optics.instances

import arrow.core.Left
import arrow.core.Right
import arrow.core.toT
import arrow.data.*
import arrow.instance
import arrow.optics.Optional
import arrow.optics.POptional
import arrow.optics.Traversal
import arrow.optics.typeclasses.Each
import arrow.optics.typeclasses.FilterIndex
import arrow.optics.typeclasses.Index

@instance(ListK::class)
interface ListKEachInstance<A> : Each<ListK<A>, A> {
  override fun each(): Traversal<ListK<A>, A> =
  // FIXME(paco) we're not scala, so we need to safely downcast
    Traversal.fromTraversable<ForListK, A, A>(ListK.traverse()) as Traversal<ListK<A>, A>
}

@instance(ListK::class)
interface ListKFilterIndexInstance<A> : FilterIndex<ListK<A>, Int, A> {
  override fun filter(p: (Int) -> Boolean): Traversal<ListK<A>, A> =
  // FIXME(paco) we're not scala, so we need to safely downcast
    FilterIndex.fromTraverse<ForListK, A>({ aas ->
      aas.fix().mapIndexed { index, a -> a toT index }.k()
    }, ListK.traverse()).filter(p) as Traversal<ListK<A>, A>
}

@instance(ListK::class)
interface ListKIndexInstance<A> : Index<ListK<A>, Int, A> {
  override fun index(i: Int): Optional<ListK<A>, A> = POptional(
    getOrModify = { it.fix().getOrNull(i)?.let(::Right) ?: it.fix().let(::Left) },
    set = { a -> { l -> l.fix().mapIndexed { index: Int, aa: A -> if (index == i) a else aa }.k() } }
  )
}