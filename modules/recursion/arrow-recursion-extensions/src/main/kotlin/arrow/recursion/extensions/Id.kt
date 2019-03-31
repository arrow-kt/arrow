package arrow.recursion.extensions

import arrow.Kind
import arrow.core.Eval
import arrow.core.Id
import arrow.core.extensions.const.functor.functor
import arrow.extension
import arrow.recursion.typeclasses.Birecursive
import arrow.typeclasses.Const
import arrow.typeclasses.ConstPartialOf
import arrow.typeclasses.Functor
import arrow.typeclasses.value

@extension
interface IdBirecursive<A> : Birecursive<Id<A>, ConstPartialOf<Id<A>>> {
  override fun FF(): Functor<ConstPartialOf<Id<A>>> = Const.functor()

  override fun Kind<ConstPartialOf<Id<A>>, Eval<Id<A>>>.embedT(): Eval<Id<A>> = Eval.now(value())
  override fun Id<A>.projectT(): Kind<ConstPartialOf<Id<A>>, Id<A>> = Const(this)
}