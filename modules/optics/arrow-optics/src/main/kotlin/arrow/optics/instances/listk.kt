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

fun <A> ListK.Companion.traversal(): Traversal<ListK<A>, A> = object : Traversal<ListK<A>, A> {
  override fun <F> modifyF(FA: Applicative<F>, s: ListK<A>, f: (A) -> Kind<F, A>): Kind<F, ListK<A>> =
    ListK.traverse().run { s.traverse(FA, f) }
}

@instance(ListK::class)
interface ListKEachInstance<A> : Each<ListK<A>, A> {
  override fun each(): Traversal<ListK<A>, A> =
    ListK.traversal()
}

@instance(ListK::class)
interface ListKFilterIndexInstance<A> : FilterIndex<ListK<A>, Int, A> {
  override fun filter(p: (Int) -> Boolean): Traversal<ListK<A>, A> = object : Traversal<ListK<A>, A> {
    override fun <F> modifyF(FA: Applicative<F>, s: ListK<A>, f: (A) -> Kind<F, A>): Kind<F, ListK<A>> =
      ListK.traverse().run {
        FA.run {
          s.mapIndexed { index, a -> a toT index }.k().traverse(FA, { (a, j) ->
            if (p(j)) f(a) else just(a)
          })
        }
      }
  }
}

@instance(ListK::class)
interface ListKIndexInstance<A> : Index<ListK<A>, Int, A> {
  override fun index(i: Int): Optional<ListK<A>, A> = POptional(
    getOrModify = { it.fix().getOrNull(i)?.let(::Right) ?: it.fix().let(::Left) },
    set = { a -> { l -> l.fix().mapIndexed { index: Int, aa: A -> if (index == i) a else aa }.k() } }
  )
}