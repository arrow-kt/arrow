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

fun <A> SequenceK.Companion.traversal(): Traversal<SequenceK<A>, A> = object : Traversal<SequenceK<A>, A> {
  override fun <F> modifyF(FA: Applicative<F>, s: SequenceK<A>, f: (A) -> Kind<F, A>): Kind<F, SequenceK<A>> = with(SequenceK.traverse()) {
    s.traverse(FA, f)
  }
}

@instance(SequenceK::class)
interface SequenceKEachInstance<A> : Each<SequenceK<A>, A> {
  override fun each(): Traversal<SequenceK<A>, A> =
    SequenceK.traversal()
}

@instance(SequenceK::class)
interface SequenceKFilterIndexInstance<A> : FilterIndex<SequenceK<A>, Int, A> {
  override fun filter(p: (Int) -> Boolean): Traversal<SequenceK<A>, A> = object : Traversal<SequenceK<A>, A> {
    override fun <F> modifyF(FA: Applicative<F>, s: SequenceK<A>, f: (A) -> Kind<F, A>): Kind<F, SequenceK<A>> =
      SequenceK.traverse().run {
        FA.run {
          s.mapIndexed { index, a -> a toT index }.k().traverse(FA, { (a, j) ->
            if (p(j)) f(a) else just(a)
          })
        }
      }
  }
}

@instance(SequenceK::class)
interface SequenceKIndexInstance<A> : Index<SequenceK<A>, Int, A> {
  override fun index(i: Int): Optional<SequenceK<A>, A> = POptional(
    getOrModify = { it.fix().sequence.elementAtOrNull(i)?.let(::Right) ?: it.fix().let(::Left) },
    set = { a -> { it.fix().mapIndexed { index, aa -> if (index == i) a else aa }.k() } }
  )
}