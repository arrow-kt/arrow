package io.github.aedans.katalyst.data

import arrow.Kind
import arrow.core.Eval
import arrow.higherkind
import arrow.instance
import arrow.typeclasses.Functor
import io.github.aedans.katalyst.typeclasses.Birecursive
import io.github.aedans.katalyst.typeclasses.Corecursive
import io.github.aedans.katalyst.typeclasses.Recursive

/**
 * Type level combinator for obtaining the fixed point of a type.
 * This type is the type level encoding of primitive recursion.
 */
@higherkind
data class Fix<out A>(val unfix: Kind<A, Eval<FixOf<A>>>) : FixOf<A> {
  companion object
}

@instance(Fix::class)
interface FixBirecursiveInstance : Birecursive<ForFix> {
  override fun <F> projectT(FF: Functor<F>, t: FixOf<F>) = FF.run {
    t.fix().unfix.map { it.value() }
  }

  override fun <F> embedT(FF: Functor<F>, t: Kind<F, Eval<FixOf<F>>>) =
    Eval.later { Fix(t) }
}

@instance(Fix::class)
interface FixRecursiveInstance : Recursive<ForFix>, FixBirecursiveInstance

@instance(Fix::class)
interface FixCorecursiveInstance : Corecursive<ForFix>, FixBirecursiveInstance
