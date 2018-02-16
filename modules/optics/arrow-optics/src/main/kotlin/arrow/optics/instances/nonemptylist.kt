package arrow.optics.instances

import arrow.core.toT
import arrow.data.ForNonEmptyList
import arrow.data.NonEmptyList
import arrow.data.NonEmptyListOf
import arrow.data.fix
import arrow.data.traverse
import arrow.instance
import arrow.optics.Optional
import arrow.optics.POptional
import arrow.optics.Traversal
import arrow.optics.typeclasses.FilterIndex
import arrow.optics.typeclasses.Index
import arrow.syntax.either.left
import arrow.syntax.either.right

@instance(NonEmptyList::class)
interface NonEmptyListFilterIndexInstance<A> : FilterIndex<NonEmptyListOf<A>, Int, A> {
    override fun filter(p: (Int) -> Boolean): Traversal<NonEmptyListOf<A>, A> = FilterIndex.fromTraverse<ForNonEmptyList, A>({ aas ->
        aas.fix().all.mapIndexed { index, a -> a toT index }.let {
            NonEmptyList.fromListUnsafe(it)
        }
    }, NonEmptyList.traverse()).filter(p)
}

@instance(NonEmptyList::class)
interface NonEmptyListIndexInstance<A> : Index<NonEmptyListOf<A>, Int, A> {

    override fun index(i: Int): Optional<NonEmptyListOf<A>, A> = POptional(
            getOrModify = { l ->
                l.fix().all.getOrNull(i)?.right() ?: l.fix().left()
            },
            set = { a ->
                { l ->
                    NonEmptyList.fromListUnsafe(
                            l.fix().all.mapIndexed { index: Int, aa: A -> if (index == i) a else aa }
                    )
                }
            }
    )
}