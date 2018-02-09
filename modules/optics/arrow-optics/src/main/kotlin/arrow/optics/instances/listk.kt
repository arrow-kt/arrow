package arrow.optics.instances

import arrow.core.toT
import arrow.data.ForListK
import arrow.data.ListK
import arrow.data.ListKOf
import arrow.data.extract
import arrow.data.k
import arrow.data.traverse
import arrow.instance
import arrow.optics.Optional
import arrow.optics.POptional
import arrow.optics.Traversal
import arrow.optics.typeclasses.Each
import arrow.optics.typeclasses.FilterIndex
import arrow.optics.typeclasses.Index
import arrow.syntax.either.left
import arrow.syntax.either.right

@instance(ListK::class)
interface ListKEachInstance<A> : Each<ListKOf<A>, A> {
    override fun each(): Traversal<ListKOf<A>, A> =
            Traversal.fromTraversable(ListK.traverse())
}

@instance(ListK::class)
interface ListKFilterIndexInstance<A> : FilterIndex<ListKOf<A>, Int, A> {
    override fun filter(p: (Int) -> Boolean): Traversal<ListKOf<A>, A> = FilterIndex.fromTraverse<ForListK, A>({ aas ->
        aas.extract().mapIndexed { index, a -> a toT index }.k()
    }, ListK.traverse()).filter(p)
}

@instance(ListK::class)
interface ListKIndexInstance<A> : Index<ListKOf<A>, Int, A> {
    override fun index(i: Int): Optional<ListKOf<A>, A> = POptional(
            getOrModify = { it.extract().getOrNull(i)?.right() ?: it.extract().left() },
            set = { a -> { l -> l.extract().mapIndexed { index: Int, aa: A -> if (index == i) a else aa }.k() } }
    )
}