package arrow.continuations

import arrow.continuations.generic.DelimitedScope

fun interface Effect<F> {
  fun control(): DelimitedScope<F>

  companion object {
    suspend inline fun <Eff : Effect<*>, F, A> suspended(
      crossinline eff: (DelimitedScope<F>) -> Eff,
      crossinline just: (A) -> F,
      crossinline f: suspend Eff.() -> A,
    ): F =
      Reset.suspended { just(f(eff(this))) }

    inline fun <Eff : Effect<*>, F, A> restricted(
      crossinline eff: (DelimitedScope<F>) -> Eff,
      crossinline just: (A) -> F,
      crossinline f: suspend Eff.() -> A,
    ): F =
      Reset.restricted { just(f(eff(this))) }
  }
}
