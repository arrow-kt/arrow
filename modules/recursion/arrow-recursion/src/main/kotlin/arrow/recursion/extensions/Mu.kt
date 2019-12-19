package arrow.recursion.extensions

import arrow.Kind
import arrow.extension
import arrow.recursion.Algebra
import arrow.recursion.data.Mu
import arrow.recursion.data.fix
import arrow.recursion.typeclasses.Birecursive
import arrow.recursion.typeclasses.Corecursive
import arrow.recursion.typeclasses.Recursive
import arrow.typeclasses.Functor

@extension
interface MuBirecursive<F> : Birecursive<Mu<F>, F> {
  override fun FF(): Functor<F>

  override fun Kind<F, Mu<F>>.embedT(): Mu<F> =
    object : Mu<F> {
      override fun <A> unMu(alg: Algebra<F, A>) = FF().run {
        alg(map { it.fix().unMu(alg) })
      }
    }

  override fun Mu<F>.projectT(): Kind<F, Mu<F>> = FF().run {
    cata { ff -> ff.map { f -> f.embedT() } }
  }

  override fun <A> Mu<F>.cata(alg: Algebra<F, A>): A = unMu(alg)
}

@extension
interface MuRecursive<F> : Recursive<Mu<F>, F>, MuBirecursive<F> {
  override fun FF(): Functor<F>
}

@extension
interface MuCorecursive<F> : Corecursive<Mu<F>, F>, MuBirecursive<F> {
  override fun FF(): Functor<F>
}
