package arrow.continuations

import arrow.continuations.generic.DelimitedScope
import arrow.continuations.generic.deprecateArrowContinuation

@Deprecated(deprecateArrowContinuation)
public fun interface Effect<F> {
  public fun control(): DelimitedScope<F>

  public companion object {
    @Deprecated("$deprecateArrowContinuation Here one can use effect { } directly")
    public suspend inline fun <Eff : Effect<*>, F, A> suspended(
      crossinline eff: (DelimitedScope<F>) -> Eff,
      crossinline just: (A) -> F,
      crossinline f: suspend Eff.() -> A,
    ): F =
      Reset.suspended { just(f(eff(this))) }

    @Deprecated("$deprecateArrowContinuation Here one can use eagerEffect { } directly")
    public inline fun <Eff : Effect<*>, F, A> restricted(
      crossinline eff: (DelimitedScope<F>) -> Eff,
      crossinline just: (A) -> F,
      crossinline f: suspend Eff.() -> A,
    ): F =
      Reset.restricted { just(f(eff(this))) }
  }
}
