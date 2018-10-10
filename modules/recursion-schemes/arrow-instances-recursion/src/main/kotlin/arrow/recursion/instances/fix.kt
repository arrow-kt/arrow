package arrow.recursion.instances

import arrow.Kind
import arrow.core.Eval
import arrow.instance
import arrow.recursion.data.Fix
import arrow.recursion.data.ForFix
import arrow.recursion.data.fix
import arrow.typeclasses.Functor
import arrow.recursion.typeclasses.Birecursive
import arrow.recursion.typeclasses.Corecursive
import arrow.recursion.typeclasses.Recursive

@instance
interface FixBirecursiveInstance : Birecursive<ForFix> {
  override fun <F> Functor<F>.projectT(tf: Kind<ForFix, F>) =
    tf.fix().unfix.map { it.value() }

  override fun <F> Functor<F>.embedT(tf: Kind<F, Eval<Kind<ForFix, F>>>) =
    Eval.later { Fix(tf) }
}

@instance
interface FixRecursiveInstance : Recursive<ForFix>, FixBirecursiveInstance

@instance
interface FixCorecursiveInstance : Corecursive<ForFix>, FixBirecursiveInstance
