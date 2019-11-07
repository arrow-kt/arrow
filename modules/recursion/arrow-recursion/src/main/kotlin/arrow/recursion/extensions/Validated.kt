package arrow.recursion.extensions

import arrow.Kind
import arrow.core.Validated
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
interface ValidatedBirecursive<E, A> : Birecursive<Validated<E, A>, ConstPartialOf<Validated<E, A>>> {
  override fun FF(): Functor<ConstPartialOf<Validated<E, A>>> = Const.functor()

  override fun Validated<E, A>.projectT(): Kind<ConstPartialOf<Validated<E, A>>, Validated<E, A>> = Const(this)
  override fun Kind<ConstPartialOf<Validated<E, A>>, Validated<E, A>>.embedT(): Validated<E, A> = value()
}

@extension
interface ValidatedRecursive<E, A> : Recursive<Validated<E, A>, ConstPartialOf<Validated<E, A>>>, ValidatedBirecursive<E, A>

@extension
interface ValidatedCorecursive<E, A> : Corecursive<Validated<E, A>, ConstPartialOf<Validated<E, A>>>, ValidatedBirecursive<E, A>
