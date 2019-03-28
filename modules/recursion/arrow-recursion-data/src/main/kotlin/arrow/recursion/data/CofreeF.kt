package arrow.recursion.data

import arrow.Kind
import arrow.higherkind
import arrow.typeclasses.Functor

@higherkind
data class CofreeF<F, A, B>(val FF: Functor<F>, val head: A, val tail: Kind<F, B>): CofreeFOf<F, A, B> {

  fun <C> map(f: (B) -> C): CofreeF<F, A, C> = CofreeF(FF, head, FF.run { tail.map(f) })

  companion object
}