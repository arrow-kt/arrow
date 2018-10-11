package arrow.recursion.instances

import arrow.Kind
import arrow.core.Eval
import arrow.extension
import arrow.recursion.data.Fix
import arrow.recursion.data.ForFix
import arrow.recursion.data.fix
import arrow.typeclasses.Functor
import arrow.recursion.typeclasses.Birecursive
import arrow.recursion.typeclasses.Corecursive
import arrow.recursion.typeclasses.Recursive

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
