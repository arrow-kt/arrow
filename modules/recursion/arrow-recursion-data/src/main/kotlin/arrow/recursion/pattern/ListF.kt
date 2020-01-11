package arrow.recursion.pattern

import arrow.Kind
import arrow.higherkind
import arrow.recursion.data.Fix
import arrow.typeclasses.Applicative

@higherkind
sealed class ListF<A, B> : ListFOf<A, B> {
  class NilF<A, B> : ListF<A, B>()
  data class ConsF<A, B>(val a: A, val tail: B) : ListF<A, B>()

  fun <S> map(f: (B) -> S): ListFOf<A, S> = when (this) {
    is NilF -> NilF()
    is ConsF -> ConsF(a, f(tail))
  }

  fun <C, G> traverse(AP: Applicative<G>, f: (B) -> Kind<G, C>): Kind<G, ListF<A, C>> =
    when (this) {
      is ConsF -> AP.run { f(tail).map { ConsF(a, it) } }
      is NilF -> AP.just(NilF())
    }

  companion object
}

typealias ListR<A> = Fix<ListFPartialOf<A>>
