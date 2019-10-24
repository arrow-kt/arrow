package arrow.recursion.extensions

import arrow.Kind
import arrow.extension
import arrow.recursion.Coalgebra
import arrow.recursion.data.Nu
import arrow.recursion.typeclasses.Birecursive
import arrow.recursion.typeclasses.Corecursive
import arrow.recursion.typeclasses.Recursive
import arrow.typeclasses.Functor

@extension
interface NuBirecursive<F> : Birecursive<Nu<F>, F> {
  override fun FF(): Functor<F>

  override fun Nu<F>.projectT(): Kind<F, Nu<F>> = FF().run {
    unNu(a).map { Nu(it, unNu) }
  }

  override fun Kind<F, Nu<F>>.embedT(): Nu<F> = ana {
    FF().run {
      it.map { it.projectT() }
    }
  }

  override fun <A> A.ana(coalg: Coalgebra<F, A>): Nu<F> = Nu(this, coalg)
}

@extension
interface NuRecursive<F> : Recursive<Nu<F>, F>, NuBirecursive<F> {
  override fun FF(): Functor<F>
}

@extension
interface NuCorecursive<F> : Corecursive<Nu<F>, F>, NuBirecursive<F> {
  override fun FF(): Functor<F>
}
