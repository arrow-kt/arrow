package arrow.optics

import arrow.Kind
import arrow.core.k
import arrow.core.left
import arrow.core.right
import arrow.core.toT
import arrow.optics.typeclasses.FilterIndex
import arrow.optics.typeclasses.Index
import arrow.typeclasses.Applicative

fun <A> FilterIndex.Companion.sequence(): FilterIndex<Sequence<A>, Int, A> = FilterIndex { p ->
  object : Traversal<Sequence<A>, A> {
    override fun <F> modifyF(FA: Applicative<F>, s: Sequence<A>, f: (A) -> Kind<F, A>): Kind<F, Sequence<A>> =
      FA.run {
        s.mapIndexed { index, a -> a toT index }.k().traverse(FA) { (a, j) ->
          if (p(j)) f(a) else just(a)
        }
      }
  }
}

fun <A> Index.Companion.sequence(): Index<Sequence<A>, Int, A> = Index { i ->
  POptional(
    getOrModify = { it.elementAtOrNull(i)?.right() ?: it.left() },
    set = { s, a -> s.mapIndexed { index, aa -> if (index == i) a else aa } }
  )
}

fun <A> PTraversal.Companion.sequence(): Traversal<Sequence<A>, A> =
  object : Traversal<Sequence<A>, A> {
    override fun <F> modifyF(FA: Applicative<F>, s: Sequence<A>, f: (A) -> Kind<F, A>): Kind<F, Sequence<A>> =
      FA.run { s.k().traverse(FA, f) }
  }
