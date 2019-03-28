package arrow.recursion.extensions

import arrow.Kind
import arrow.core.Eval
import arrow.core.extensions.const.functor.functor
import arrow.data.Ior
import arrow.extension
import arrow.recursion.typeclasses.Birecursive
import arrow.typeclasses.Const
import arrow.typeclasses.ConstPartialOf
import arrow.typeclasses.Functor
import arrow.typeclasses.fix

@extension
interface IorBirecursive<L, R> : Birecursive<Ior<L, R>, ConstPartialOf<Ior<L, R>>> {
  override fun FF(): Functor<ConstPartialOf<Ior<L, R>>> = Const.functor()

  override fun Ior<L, R>.projectT(): Kind<ConstPartialOf<Ior<L, R>>, Ior<L, R>> = Const(this)
  override fun Kind<ConstPartialOf<Ior<L, R>>, Eval<Ior<L, R>>>.embedT(): Eval<Ior<L, R>> = Eval.now(fix().value())
}