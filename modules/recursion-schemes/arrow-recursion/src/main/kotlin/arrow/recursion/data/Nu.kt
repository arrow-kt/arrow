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
  override fun <F> Functor<F>.projectT(tf: Kind<ForNu, F>): Kind<F, Nu<F>> {
    val fix = tf.fix()
    val unNu = fix.unNu
    return unNu(fix.a).map { Nu(it, unNu) }
  }

  override fun <F> Functor<F>.embedT(tf: Kind<F, Eval<Kind<ForNu, F>>>) =
    Eval.now(Nu.invoke(tf) { f -> f.map { nu -> projectT(nu.value()).map(::Now) } })

  override fun <F, A> Functor<F>.ana(a: A, coalg: Coalgebra<F, A>) =
    Nu(a, coalg)
}

@instance(Nu::class)
interface NuRecursiveInstance : Recursive<ForNu>, NuBirecursiveInstance

@instance(Nu::class)
interface NuCorecursiveInstance : Corecursive<ForNu>, NuBirecursiveInstance
