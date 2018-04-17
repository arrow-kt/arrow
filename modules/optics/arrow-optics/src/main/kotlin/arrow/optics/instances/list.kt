package arrow.optics.instances

import arrow.Kind
import arrow.core.Left
import arrow.core.Right
import arrow.core.toT
import arrow.data.ListK
import arrow.data.k
import arrow.data.traverse
import arrow.optics.Optional
import arrow.optics.POptional
import arrow.optics.Traversal
import arrow.optics.typeclasses.Each
import arrow.optics.typeclasses.FilterIndex
import arrow.optics.typeclasses.Index
import arrow.typeclasses.Applicative

interface ListTraversal<A> : Traversal<List<A>, A> {

  override fun <F> modifyF(FA: Applicative<F>, s: List<A>, f: (A) -> Kind<F, A>): Kind<F, List<A>> = with(ListK.traverse()) {
    FA.run { s.k().traverse(FA, f).map { it.list } }
  }

  companion object {
    operator fun <A> invoke() = object : ListTraversal<A> {}
  }

}

interface ListEachInstance<A> : Each<List<A>, A> {
  override fun each() = ListTraversal<A>()

  companion object {
    operator fun <A> invoke() = object : ListEachInstance<A> {}
  }
}

interface ListFilterIndexInstance<A> : FilterIndex<List<A>, Int, A> {
  override fun filter(p: (Int) -> Boolean): Traversal<List<A>, A> = object : Traversal<List<A>, A> {
    override fun <F> modifyF(FA: Applicative<F>, s: List<A>, f: (A) -> Kind<F, A>): Kind<F, List<A>> =
      ListK.traverse().run {
        FA.run {
          s.mapIndexed { index, a -> a toT index }.k().traverse(this, { (a, j) ->
            if (p(j)) f(a) else just(a)
          }).map { it.list }
        }
      }
  }

  companion object {
    operator fun <A> invoke() = object : ListFilterIndexInstance<A> {}
  }
}

interface ListIndexInstance<A> : Index<List<A>, Int, A> {
  override fun index(i: Int): Optional<List<A>, A> = POptional(
    getOrModify = { it.getOrNull(i)?.let(::Right) ?: it.let(::Left) },
    set = { a -> { l -> l.mapIndexed { index: Int, aa: A -> if (index == i) a else aa } } }
  )

  companion object {
    operator fun <A> invoke() = object : ListIndexInstance<A> {}
  }
}
