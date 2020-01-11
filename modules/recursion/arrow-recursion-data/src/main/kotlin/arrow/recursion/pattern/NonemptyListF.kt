package arrow.recursion.pattern

import arrow.Kind
import arrow.core.Option
import arrow.core.none
import arrow.core.some
import arrow.higherkind
import arrow.recursion.data.Fix
import arrow.typeclasses.Applicative

@higherkind
data class NonEmptyListF<A, B>(val head: A, val tail: Option<B>) : NonEmptyListFOf<A, B> {

  fun <C> map(f: (B) -> C): NonEmptyListF<A, C> = NonEmptyListF(head, tail.map(f))

  fun <C, G> traverse(AP: Applicative<G>, f: (B) -> Kind<G, C>): Kind<G, NonEmptyListF<A, C>> =
    tail.fold({ AP.just(NonEmptyListF(fix().head, none())) }, {
      AP.run { f(it).map { NonEmptyListF(fix().head, it.some()) } }
    })

  companion object
}

typealias NonEmptyListR<A> = Fix<NonEmptyListFPartialOf<A>>
