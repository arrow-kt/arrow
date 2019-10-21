package arrow.recursion.extensions

import arrow.Kind
import arrow.core.Option
import arrow.core.extensions.const.functor.functor
import arrow.extension
import arrow.recursion.typeclasses.Birecursive
import arrow.recursion.typeclasses.Corecursive
import arrow.recursion.typeclasses.Recursive
import arrow.typeclasses.Const
import arrow.typeclasses.ConstPartialOf
import arrow.typeclasses.Functor
import arrow.typeclasses.value

@extension
interface OptionBirecursive<A> : Birecursive<Option<A>, ConstPartialOf<Option<A>>> {
  override fun FF(): Functor<ConstPartialOf<Option<A>>> = Const.functor()

  override fun Option<A>.projectT(): Kind<ConstPartialOf<Option<A>>, Option<A>> = Const(this)
  override fun Kind<ConstPartialOf<Option<A>>, Option<A>>.embedT(): Option<A> = value()
}

@extension
interface OptionRecursive<A> : Recursive<Option<A>, ConstPartialOf<Option<A>>>, OptionBirecursive<A>

@extension
interface OptionCorecursive<A> : Corecursive<Option<A>, ConstPartialOf<Option<A>>>, OptionBirecursive<A>
