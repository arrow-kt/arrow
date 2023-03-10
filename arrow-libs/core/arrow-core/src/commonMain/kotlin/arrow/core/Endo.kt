package arrow.core

/**
 * The monoid of endomorphisms under composition.
 */
@Deprecated("Semigroup, and Monoid are being deprecated. Use regular function composition instead.")
public data class Endo<A>(val f: (A) -> A) {

  public fun combine(g: Endo<A>): Endo<A> =
    Endo(f.compose(g.f))

  public companion object
}
