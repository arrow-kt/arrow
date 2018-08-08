package arrow.recursion.data

import arrow.Kind
import arrow.core.Eval
import arrow.higherkind
import arrow.extension
import arrow.typeclasses.Functor
import arrow.recursion.typeclasses.Birecursive
import arrow.recursion.typeclasses.Corecursive
import arrow.recursion.typeclasses.Recursive

/**
 * Type level combinator for obtaining the fixed point of a type.
 * This type is the type level encoding of primitive recursion.
 */
@higherkind
data class Fix<out A>(val unfix: Kind<A, Eval<FixOf<A>>>) : FixOf<A> {
  companion object
}

@extension
interface FixBirecursiveInstance : Birecursive<ForFix> {
  override fun <F> Functor<F>.projectT(tf: Kind<ForFix, F>) =
    tf.fix().unfix.map { it.value() }

  override fun <F> Functor<F>.embedT(tf: Kind<F, Eval<Kind<ForFix, F>>>) =
    Eval.later { Fix(tf) }
}

@extension
interface FixRecursiveInstance : Recursive<ForFix>, FixBirecursiveInstance

@extension
interface FixCorecursiveInstance : Corecursive<ForFix>, FixBirecursiveInstance
