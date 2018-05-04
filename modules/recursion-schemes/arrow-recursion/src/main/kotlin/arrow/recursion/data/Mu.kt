package arrow.recursion.data

import arrow.Kind
import arrow.core.Eval
import arrow.core.Eval.Now
import arrow.higherkind
import arrow.instance
import arrow.typeclasses.Functor
import arrow.recursion.Algebra
import arrow.recursion.typeclasses.Birecursive
import arrow.recursion.typeclasses.Corecursive
import arrow.recursion.typeclasses.Recursive

/**
 * Type level combinator for obtaining the least fixed point of a type.
 * This type is the type level encoding of cata.
 */
@higherkind
abstract class Mu<out F> : MuOf<F> {
  abstract fun <A> unMu(fa: Algebra<F, Eval<A>>): Eval<A>

  companion object
}

@instance(Mu::class)
interface MuBirecursiveInstance : Birecursive<ForMu> {
  override fun <F> embedT(FF: Functor<F>, t: Kind<F, Eval<MuOf<F>>>): Eval<Mu<F>> = FF.run {
    Eval.now(object : Mu<F>() {
      override fun <A> unMu(fa: Algebra<F, Eval<A>>) =
        fa(t.map { it.flatMap { it.fix().unMu(fa) } })
    })
  }

  override fun <F> projectT(FF: Functor<F>, t: MuOf<F>): Kind<F, MuOf<F>> = FF.run {
    t.cata(FF) { ff ->
      Eval.later {
        ff.map { f ->
          embedT(FF, f.value().map(::Now)).value()
        }
      }
    }
  }

  override fun <F, A> MuOf<F>.cata(FF: Functor<F>, alg: Algebra<F, Eval<A>>): A =
    fix().unMu(alg).value()
}

@instance(Mu::class)
interface MuRecursiveInstance : Recursive<ForMu>, MuBirecursiveInstance

@instance(Mu::class)
interface MuCorecursiveInstance : Corecursive<ForMu>, MuBirecursiveInstance
