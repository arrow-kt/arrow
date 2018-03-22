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
interface ListKEachInstance<A> : Each<ListKOf<A>, A> {
    override fun each(): Traversal<ListKOf<A>, A> =
            Traversal.fromTraversable(ListK.traverse())
}

@instance(ListK::class)
interface ListKFilterIndexInstance<A> : FilterIndex<ListKOf<A>, Int, A> {
    override fun filter(p: (Int) -> Boolean): Traversal<ListKOf<A>, A> = FilterIndex.fromTraverse<ForListK, A>({ aas ->
        aas.fix().mapIndexed { index, a -> a toT index }.k()
    }, ListK.traverse()).filter(p)
}

@instance(ListK::class)
interface ListKIndexInstance<A> : Index<ListKOf<A>, Int, A> {
    override fun index(i: Int): Optional<ListKOf<A>, A> = POptional(
            getOrModify = { it.fix().getOrNull(i)?.let(::Right) ?: it.fix().let(::Left) },
            set = { a -> { l -> l.fix().mapIndexed { index: Int, aa: A -> if (index == i) a else aa }.k() } }
    )
}