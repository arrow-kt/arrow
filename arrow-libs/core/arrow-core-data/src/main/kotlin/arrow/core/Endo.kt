package arrow.core

/**
 * The monoid of endomorphisms under composition.
 */
data class Endo<A>(val f: (A) -> A) {

  companion object

  fun combine(g: Endo<A>): Endo<A> =
    Endo(f.compose(g.f))
}
