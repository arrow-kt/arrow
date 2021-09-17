package arrow.continuations

import arrow.continuations.generic.DelimitedScope

public fun interface Effect<F> {
  public fun control(): DelimitedScope<F>

  public companion object {
    public suspend inline fun <Eff : Effect<*>, F, A> suspended(
      crossinline eff: (DelimitedScope<F>) -> Eff,
      crossinline just: (A) -> F,
      crossinline f: suspend Eff.() -> A,
    ): F =
      Reset.suspended { just(f(eff(this))) }

    public inline fun <Eff : Effect<*>, F, A> restricted(
      crossinline eff: (DelimitedScope<F>) -> Eff,
      crossinline just: (A) -> F,
      crossinline f: suspend Eff.() -> A,
    ): F =
      Reset.restricted { just(f(eff(this))) }
  }
}
