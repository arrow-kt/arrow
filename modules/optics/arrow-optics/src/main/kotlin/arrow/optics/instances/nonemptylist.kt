package arrow.optics.instances

import arrow.Kind
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
import arrow.typeclasses.Applicative

fun <A> NonEmptyList.Companion.traversal(): Traversal<NonEmptyList<A>, A> = object : Traversal<NonEmptyList<A>, A> {
  override fun <F> modifyF(FA: Applicative<F>, s: NonEmptyList<A>, f: (A) -> Kind<F, A>): Kind<F, NonEmptyList<A>> = with(NonEmptyList.traverse()) {
    s.traverse(FA, f)
  }
}

@instance(NonEmptyList::class)
interface NonEmptyListEachInstance<A> : Each<NonEmptyList<A>, A> {
  override fun each(): Traversal<NonEmptyList<A>, A> =
    NonEmptyList.traversal()
}

@instance(NonEmptyList::class)
interface NonEmptyListFilterIndexInstance<A> : FilterIndex<NonEmptyList<A>, Int, A> {
  override fun filter(p: (Int) -> Boolean): Traversal<NonEmptyList<A>, A> = object : Traversal<NonEmptyList<A>, A> {
    override fun <F> modifyF(FA: Applicative<F>, s: NonEmptyList<A>, f: (A) -> Kind<F, A>): Kind<F, NonEmptyList<A>> = NonEmptyList.traverse().run {
      FA.run {
        s.fix().all.mapIndexed { index, a -> a toT index }.let {
          NonEmptyList.fromListUnsafe(it)
        }.traverse(FA) { (a, j) -> if (p(j)) f(a) else just(a) }
      }
    }

  }
}

@instance(NonEmptyList::class)
interface NonEmptyListIndexInstance<A> : Index<NonEmptyList<A>, Int, A> {
  override fun index(i: Int): Optional<NonEmptyList<A>, A> = POptional(
    getOrModify = { l -> l.all.getOrNull(i)?.let(::Right) ?: l.fix().let(::Left) },
    set = { a ->
      { l ->
        NonEmptyList.fromListUnsafe(
          l.all.mapIndexed { index: Int, aa: A -> if (index == i) a else aa }
        )
      }
    }
  )
}