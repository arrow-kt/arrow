package arrow.typeclasses

import arrow.Kind2

/**
 * The `EqK2` typeclass abstracts the ability to lift the Eq class to binary type constructors.
 */
@Deprecated("Kind/type constructors will be deprecated, so this typeclass will no longer be available from 0.13.0")
interface EqK2<F> {

  fun <A, B> Kind2<F, A, B>.eqK(other: Kind2<F, A, B>, EQA: Eq<A>, EQB: Eq<B>): Boolean

  fun <A, B> liftEq(EQA: Eq<A>, EQB: Eq<B>): Eq<Kind2<F, A, B>> =
    Eq { a, b ->
      this@EqK2.run { a.eqK(b, EQA, EQB) }
    }
}
