package arrow.recursion.extensions

import arrow.Kind
import arrow.core.Ior
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
interface IorBirecursive<L, R> : Birecursive<Ior<L, R>, ConstPartialOf<Ior<L, R>>> {
  override fun FF(): Functor<ConstPartialOf<Ior<L, R>>> = Const.functor()

  override fun Ior<L, R>.projectT(): Kind<ConstPartialOf<Ior<L, R>>, Ior<L, R>> = Const(this)
  override fun Kind<ConstPartialOf<Ior<L, R>>, Ior<L, R>>.embedT(): Ior<L, R> = value()
}

@extension
interface IorRecursive<L, R> : Recursive<Ior<L, R>, ConstPartialOf<Ior<L, R>>>, IorBirecursive<L, R>

@extension
interface IorCorecursive<L, R> : Corecursive<Ior<L, R>, ConstPartialOf<Ior<L, R>>>, IorBirecursive<L, R>
