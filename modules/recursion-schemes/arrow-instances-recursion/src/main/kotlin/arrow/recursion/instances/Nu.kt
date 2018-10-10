package arrow.recursion.instances

import arrow.Kind
import arrow.core.Eval
import arrow.core.Eval.Now
import arrow.instance
import arrow.typeclasses.Functor
import arrow.recursion.Coalgebra
import arrow.recursion.data.ForNu
import arrow.recursion.data.Nu
import arrow.recursion.data.fix
import arrow.recursion.typeclasses.Birecursive
import arrow.recursion.typeclasses.Corecursive
import arrow.recursion.typeclasses.Recursive

@instance
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

@instance
interface NuRecursiveInstance : Recursive<ForNu>, NuBirecursiveInstance

@instance
interface NuCorecursiveInstance : Corecursive<ForNu>, NuBirecursiveInstance
