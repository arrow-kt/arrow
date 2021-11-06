package arrow.continuations

import arrow.continuations.generic.DelimitedScope

@Deprecated("Prefer using Cont<R, A>")
public fun interface Effect<F> {
  @Deprecated("Use ContEffect<E>.shift instead")
  public fun control(): DelimitedScope<F>

  public companion object {
    // Replace by restrictedCont
    public suspend inline fun <Eff : Effect<*>, F, A> suspended(
      crossinline eff: (DelimitedScope<F>) -> Eff,
      crossinline just: (A) -> F,
      crossinline f: suspend Eff.() -> A,
    ): F =
      Reset.suspended { just(f(eff(this))) }

    // Replace by cont<R, A>
    public inline fun <Eff : Effect<*>, F, A> restricted(
      crossinline eff: (DelimitedScope<F>) -> Eff,
      crossinline just: (A) -> F,
      crossinline f: suspend Eff.() -> A,
    ): F =
      Reset.restricted { just(f(eff(this))) }
  }
}
