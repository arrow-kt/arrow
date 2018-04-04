package arrow.core

import arrow.Kind

inline operator fun <F, G, A> FunctionK<F, G>.invoke(ff: FunctionK<F, G>.() -> A) =
  run(ff)

interface FunctionK<F, G> {

  /**
   * Applies this functor transformation from `F` to `G`
   */
  operator fun <A> invoke(fa: Kind<F, A>): Kind<G, A>

  companion object {
    fun <F> id(): FunctionK<F, F> = object : FunctionK<F, F> {
      override fun <A> invoke(fa: Kind<F, A>): Kind<F, A> = fa
    }
  }
}
