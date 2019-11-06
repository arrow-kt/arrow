package arrow.recursion.extensions

import arrow.Kind
import arrow.core.Eval.Companion.now
import arrow.extension
import arrow.recursion.data.Fix
import arrow.recursion.data.fix
import arrow.recursion.typeclasses.Birecursive
import arrow.recursion.typeclasses.Corecursive
import arrow.recursion.typeclasses.Recursive
import arrow.typeclasses.Functor

@extension
interface FixBirecursive<F> : Birecursive<Fix<F>, F> {
  override fun FF(): Functor<F>

  override fun Fix<F>.projectT(): Kind<F, Fix<F>> = FF().run {
    unfix.map { it.value().fix() }
  }

  override fun Kind<F, Fix<F>>.embedT(): Fix<F> = FF().run {
    Fix(this@embedT.map(::now))
  }
}

@extension
interface FixRecursive<F> : Recursive<Fix<F>, F>, FixBirecursive<F> {
  override fun FF(): Functor<F>
}

@extension
interface FixCorecursive<F> : Corecursive<Fix<F>, F>, FixBirecursive<F> {
  override fun FF(): Functor<F>
}
