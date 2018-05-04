package arrow.recursion.data

import arrow.Kind
import arrow.core.Eval
import arrow.core.Eval.Now
import arrow.higherkind
import arrow.instance
import arrow.typeclasses.Functor
import arrow.recursion.Coalgebra
import arrow.recursion.typeclasses.Birecursive
import arrow.recursion.typeclasses.Corecursive
import arrow.recursion.typeclasses.Recursive

/**
 * Type level combinator for obtaining the greatest fixed point of a type.
 * This type is the type level encoding of ana.
 */
@higherkind
class Nu<out F>(val a: Any?, val unNu: Coalgebra<F, Any?>) : NuOf<F> {
  companion object {
    // Necessary because of Coalgebra's variance
    @Suppress("UNCHECKED_CAST")
    operator fun <F, A> invoke(a: A, unNu: Coalgebra<F, A>) = Nu(a) { it -> unNu(it as A) }
  }
}

@instance(Nu::class)
interface NuBirecursiveInstance : Birecursive<ForNu> {
  override fun <F> projectT(FF: Functor<F>, t: NuOf<F>): Kind<F, Nu<F>> = FF.run {
    val fix = t.fix()
    val unNu = fix.unNu
    unNu(fix.a).map { Nu(it, unNu) }
  }

  override fun <F> embedT(FF: Functor<F>, t: Kind<F, Eval<NuOf<F>>>) = FF.run {
    Eval.now(Nu.invoke(t) { f -> f.map { nu -> projectT(FF, nu.value()).map(::Now) } })
  }

  override fun <F, A> A.ana(FF: Functor<F>, coalg: Coalgebra<F, A>) =
    Nu(this, coalg)
}

@instance(Nu::class)
interface NuRecursiveInstance : Recursive<ForNu>, NuBirecursiveInstance

@instance(Nu::class)
interface NuCorecursiveInstance : Corecursive<ForNu>, NuBirecursiveInstance
