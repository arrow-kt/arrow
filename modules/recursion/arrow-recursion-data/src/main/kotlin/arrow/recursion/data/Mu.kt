package arrow.recursion.data

import arrow.core.Eval
import arrow.higherkind
import arrow.recursion.Algebra

/**
 * Type level combinator for obtaining the least fixed point of a type.
 * This type is the type level encoding of cata.
 */
@higherkind
abstract class Mu<out F> : MuOf<F> {
  abstract fun <A> unMu(fa: Algebra<F, Eval<A>>): Eval<A>

  companion object
}
