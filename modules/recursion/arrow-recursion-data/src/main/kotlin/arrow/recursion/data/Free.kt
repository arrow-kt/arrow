package arrow.recursion.data

import arrow.Kind
import arrow.core.Eval
import arrow.higherkind
import arrow.typeclasses.Functor

@higherkind
sealed class FreeF<E, F, A> : FreeFOf<E, F, A> {
  data class Pure<E, F, A>(val e: E) : FreeF<E, F, A>()
  data class Impure<E, F, A>(val fa: Kind<F, A>) : FreeF<E, F, A>()

  fun <B> map(FF: Functor<F>, f: (A) -> B): FreeF<E, F, B> = when (this) {
    is Pure -> Pure(e)
    is Impure -> Impure(FF.run { fa.map(f) })
  }

  companion object {
    fun <F, A> pure(a: A): FreeR<F, A> = Fix(Pure(a))
    fun <F, A> impure(fa: Kind<F, Eval<FreeR<F, A>>>): FreeR<F, A> = Fix(Impure(fa))
  }
}

typealias FreeR<F, A> = Fix<FreeFPartialOf<A, F>>