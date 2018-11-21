package arrow.typeclasses

import arrow.Kind

/**
 * ank_macro_hierarchy(arrow.typeclasses.MonoidK)
 *
 * MonoidK is a universal monoid which operates on kinds.
 *
 * MonoidK<F> allows two F<A> values to be combined, for any A. It also means that for any A, there
 * is an "empty" F<A> value.
 */
interface MonoidK<F> : SemigroupK<F> {

  /**
   * Given a type A, create an "empty" F<A> value.
   */
  fun <A> empty(): Kind<F, A>

  override fun <A> algebra(): Monoid<Kind<F, A>> = object : Monoid<Kind<F, A>> {

    override fun empty(): Kind<F, A> = this@MonoidK.empty()

    override fun Kind<F, A>.combine(b: Kind<F, A>): Kind<F, A> = this.combineK(b)
  }
}