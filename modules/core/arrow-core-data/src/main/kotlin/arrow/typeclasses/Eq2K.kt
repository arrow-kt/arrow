package arrow.typeclasses

import arrow.Kind2

/**
 * The `Eq2K` typeclass abstracts the ability to lift the Eq class to binary type constructors.
 */
interface Eq2K<F> {

  fun <A, B> Kind2<F, A, B>.eqK(other: Kind2<F, A, B>, EQA: Eq<A>, EQB: Eq<B>): Boolean

  fun <A, B> liftEq(EQA: Eq<A>, EQB: Eq<B>): Eq<Kind2<F, A, B>> =
    Eq { a, b ->
      this@Eq2K.run { a.eqK(b, EQA, EQB) }
    }
}
