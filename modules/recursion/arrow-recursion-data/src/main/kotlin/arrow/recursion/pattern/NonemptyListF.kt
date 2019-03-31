package arrow.recursion.pattern

import arrow.core.Option
import arrow.higherkind

@higherkind
data class NonEmptyListF<A, B>(val head: A, val tail: Option<B>) : NonEmptyListFOf<A, B> {

  fun <C> map(f: (B) -> C): NonEmptyListF<A, C> = NonEmptyListF(head, tail.map(f))

  companion object
}