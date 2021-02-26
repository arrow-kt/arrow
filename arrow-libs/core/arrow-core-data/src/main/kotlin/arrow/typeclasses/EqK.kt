package arrow.typeclasses

import arrow.Kind

/**
 * The `EqK` typeclass abstracts the ability to lift the Eq class to unary type constructors.
 */
@Deprecated("Kind/type constructors will be deprecated, so this typeclass will no longer be available from 0.13.0")
interface EqK<F> {

  fun <A> Kind<F, A>.eqK(other: Kind<F, A>, EQ: Eq<A>): Boolean

  fun <A> liftEq(EQ: Eq<A>): Eq<Kind<F, A>> =
    Eq { a, b ->
      this@EqK.run { a.eqK(b, EQ) }
    }
}
