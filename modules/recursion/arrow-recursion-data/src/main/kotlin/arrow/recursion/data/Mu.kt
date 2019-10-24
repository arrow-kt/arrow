package arrow.recursion.data

import arrow.higherkind
import arrow.recursion.Algebra

/**
 * Type level combinator for obtaining the least fixed point of a type.
 * This type is the type level encoding of cata.
 */
@higherkind
interface Mu<F> : MuOf<F> {
  fun <A> unMu(alg: Algebra<F, A>): A

  companion object
}
