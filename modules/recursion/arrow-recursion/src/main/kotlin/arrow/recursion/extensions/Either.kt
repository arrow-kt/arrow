package arrow.recursion.extensions

import arrow.Kind
import arrow.core.Const
import arrow.core.ConstPartialOf
import arrow.core.Either
import arrow.core.extensions.const.functor.functor
import arrow.core.value
import arrow.extension
import arrow.recursion.typeclasses.Birecursive
import arrow.recursion.typeclasses.Corecursive
import arrow.recursion.typeclasses.Recursive
import arrow.typeclasses.Functor

@extension
interface EitherRecursive<L, R> : Recursive<Either<L, R>, ConstPartialOf<Either<L, R>>> {
  override fun FF(): Functor<ConstPartialOf<Either<L, R>>> = Const.functor()

  override fun Either<L, R>.projectT(): Kind<ConstPartialOf<Either<L, R>>, Either<L, R>> =
    Const(this)
}

@extension
interface EitherCorecursive<L, R> : Corecursive<Either<L, R>, ConstPartialOf<Either<L, R>>> {
  override fun FF(): Functor<ConstPartialOf<Either<L, R>>> = Const.functor()

  override fun Kind<ConstPartialOf<Either<L, R>>, Either<L, R>>.embedT(): Either<L, R> =
    value()
}

@extension
interface EitherBirecursive<L, R> : Birecursive<Either<L, R>, ConstPartialOf<Either<L, R>>>, EitherRecursive<L, R>, EitherCorecursive<L, R> {
  override fun FF(): Functor<ConstPartialOf<Either<L, R>>> = Const.functor()
}
