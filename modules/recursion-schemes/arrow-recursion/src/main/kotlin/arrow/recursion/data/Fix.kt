package arrow.recursion.data

import arrow.Kind
import arrow.core.Eval
import arrow.higherkind

/**
 * Type level combinator for obtaining the fixed point of a type.
 * This type is the type level encoding of primitive recursion.
 */
@higherkind
data class Fix<out A>(val unfix: Kind<A, Eval<FixOf<A>>>) : FixOf<A> {
  companion object
}