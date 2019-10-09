package arrow.core

import arrow.Kind

interface FunctionK<F, G> {

  /**
   * Applies this functor transformation from `F` to `G`
   */
  operator fun <A> invoke(fa: Kind<F, A>): Kind<G, A>

  companion object {
    fun <F> id(): FunctionK<F, F> = object : FunctionK<F, F> {
      override fun <A> invoke(fa: Kind<F, A>): Kind<F, A> = fa
    }
    
    operator fun <F, G> invoke(f: (Kind<F, A>) -> Kind<G, A>): FunctionK<F, G> = object : FunctionK<F, G> {
      override fun <A> invoke(fa: Kind<F, A>): Kind<G, A> = f(fa)
    }
  }
}
