package arrow.recursion.extensions

import arrow.Kind
import arrow.core.Eval
import arrow.core.Eval.Now
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

  override fun Kind<F, Eval<Mu<F>>>.embedT(): Eval<Mu<F>> =
    Eval.now(object : Mu<F> {
      override fun <A> unMu(alg: Algebra<F, Eval<A>>) = FF().run {
        alg(map { it.flatMap { it.fix().unMu(alg) } })
      }
    })

  override fun Mu<F>.projectT(): Kind<F, Mu<F>> = FF().run {
    cata { ff -> Eval.later { ff.map { f -> f.value().map(::Now).embedT().value() } } }
  }

  override fun <A> Mu<F>.cata(alg: Algebra<F, Eval<A>>): A =
    unMu(alg).value()
}

@extension
interface MuRecursive<F> : Recursive<Mu<F>, F>, MuBirecursive<F> {
  override fun FF(): Functor<F>
}

@extension
interface MuCorecursive<F> : Corecursive<Mu<F>, F>, MuBirecursive<F> {
  override fun FF(): Functor<F>
}
