package arrow.typeclasses

import arrow.Kind

@Deprecated("Kind/type constructors will be deprecated, so this typeclass will no longer be available from 0.13.0")
interface SemigroupK<F> {

  /**
   * Combine two F<A> values.
   */
  fun <A> Kind<F, A>.combineK(y: Kind<F, A>): Kind<F, A>

  /**
   * Given a type A, create a concrete Semigroup<F<A>>.
   */
  fun <A> algebra(): Semigroup<Kind<F, A>> = object : Semigroup<Kind<F, A>> {
    override fun Kind<F, A>.combine(b: Kind<F, A>): Kind<F, A> =
      this.combineK(b)
  }
}
