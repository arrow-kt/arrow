package arrow.recursion.instances

import arrow.Kind
import arrow.core.Eval
import arrow.core.Eval.Now
import arrow.extension
import arrow.typeclasses.Functor
import arrow.recursion.Algebra
import arrow.recursion.data.ForMu
import arrow.recursion.data.Mu
import arrow.recursion.data.MuOf
import arrow.recursion.data.fix
import arrow.recursion.typeclasses.Birecursive
import arrow.recursion.typeclasses.Corecursive
import arrow.recursion.typeclasses.Recursive

@extension
interface MuBirecursiveInstance : Birecursive<ForMu> {
  override fun <F> Functor<F>.embedT(tf: Kind<F, Eval<Kind<ForMu, F>>>): Eval<Mu<F>> =
    Eval.now(object : Mu<F>() {
      override fun <A> unMu(fa: Algebra<F, Eval<A>>) =
        fa(tf.map { it.flatMap { it.fix().unMu(fa) } })
    })

  override fun <F> Functor<F>.projectT(tf: Kind<ForMu, F>): Kind<F, MuOf<F>> =
    cata(tf) { ff -> Eval.later { ff.map { f -> embedT(f.value().map(::Now)).value() } } }

  override fun <F, A> Functor<F>.cata(tf: Kind<ForMu, F>, alg: Algebra<F, Eval<A>>): A =
    tf.fix().unMu(alg).value()
}

@extension
interface MuRecursiveInstance : Recursive<ForMu>, MuBirecursiveInstance

@extension
interface MuCorecursiveInstance : Corecursive<ForMu>, MuBirecursiveInstance
