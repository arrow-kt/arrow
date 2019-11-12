package arrow.recursion.extensions

import arrow.Kind
import arrow.core.Const
import arrow.core.ConstPartialOf
import arrow.core.Id
import arrow.core.extensions.const.functor.functor
import arrow.core.value
import arrow.extension
import arrow.recursion.typeclasses.Birecursive
import arrow.recursion.typeclasses.Corecursive
import arrow.recursion.typeclasses.Recursive
import arrow.typeclasses.Functor

@extension
interface IdBirecursive<A> : Birecursive<Id<A>, ConstPartialOf<Id<A>>> {
  override fun FF(): Functor<ConstPartialOf<Id<A>>> = Const.functor()

  override fun Kind<ConstPartialOf<Id<A>>, Id<A>>.embedT(): Id<A> = value()
  override fun Id<A>.projectT(): Kind<ConstPartialOf<Id<A>>, Id<A>> = Const(this)
}

@extension
interface IdRecursive<A> : Recursive<Id<A>, ConstPartialOf<Id<A>>>, IdBirecursive<A>

@extension
interface IdCorecursive<A> : Corecursive<Id<A>, ConstPartialOf<Id<A>>>, IdBirecursive<A>
