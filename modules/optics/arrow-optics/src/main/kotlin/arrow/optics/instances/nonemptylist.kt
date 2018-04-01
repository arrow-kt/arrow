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
      l.fix().all.getOrNull(i)?.let(::Right) ?: l.fix().let(::Left)
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