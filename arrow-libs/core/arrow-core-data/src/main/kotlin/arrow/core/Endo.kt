package arrow.core

import arrow.typeclasses.Monoid

/**
 * The monoid of endomorphisms under composition.
 */
data class Endo<A>(val f: (A) -> A) {

  companion object

  fun combine(g: Endo<A>): Endo<A> =
    Endo(f.compose(g.f))
}

fun <A> Monoid.Companion.endo(): Monoid<Endo<A>> = object : Monoid<Endo<A>> {
  override fun empty(): Endo<A> =
    Endo(::identity)

  override fun Endo<A>.combine(g: Endo<A>): Endo<A> =
    Endo(f.compose(g.f))
}
