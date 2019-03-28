package arrow.recursion.extensions

import arrow.Kind
import arrow.core.Eval
import arrow.extension
import arrow.free.Cofree
import arrow.recursion.data.CofreeF
import arrow.recursion.data.CofreeFPartialOf
import arrow.recursion.data.fix
import arrow.recursion.extensions.cofreef.functor.functor
import arrow.recursion.typeclasses.Birecursive
import arrow.typeclasses.Functor

@extension
interface CofreeFFunctor<F, I> : Functor<CofreeFPartialOf<F, I>> {
  override fun <B, C> Kind<CofreeFPartialOf<F, I>, B>.map(f: (B) -> C): Kind<CofreeFPartialOf<F, I>, C> =
    fix().map(f)
}

@extension
interface CofreeBirecursive<S, A> : Birecursive<Cofree<S, A>, CofreeFPartialOf<S, A>> {
  fun SF(): Functor<S>
  override fun FF(): Functor<CofreeFPartialOf<S, A>> = CofreeF.functor()

  override fun Cofree<S, A>.projectT(): Kind<CofreeFPartialOf<S, A>, Cofree<S, A>> =
    CofreeF(SF(), head, tail.value())

  override fun Kind<CofreeFPartialOf<S, A>, Eval<Cofree<S, A>>>.embedT(): Eval<Cofree<S, A>> = fix().run {
    Eval.later { Cofree(SF(), head, SF().run { Eval.later { tail.map { it.value() } } }) }
  }
}