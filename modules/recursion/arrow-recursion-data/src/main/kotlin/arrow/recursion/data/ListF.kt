package arrow.recursion.data

import arrow.higherkind

@higherkind
sealed class ListF<A, B> : ListFOf<A, B> {
  class NilF<A, B> : ListF<A, B>()
  data class ConsF<A, B>(val a: A, val tail: B) : ListF<A, B>()

  fun <S> map(f: (B) -> S): ListFOf<A, S> = when (this) {
    is NilF -> NilF()
    is ConsF -> ConsF(a, f(tail))
  }

  companion object
}