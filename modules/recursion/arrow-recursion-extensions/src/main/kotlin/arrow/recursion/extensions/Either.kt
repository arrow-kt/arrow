package arrow.recursion.extensions

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.extensions.const.functor.functor
import arrow.extension
import arrow.recursion.typeclasses.Birecursive
import arrow.typeclasses.Const
import arrow.typeclasses.ConstPartialOf
import arrow.typeclasses.Functor
import arrow.typeclasses.value

@extension
interface EitherBirecursive<L, R> : Birecursive<Either<L, R>, ConstPartialOf<Either<L, R>>> {
  override fun FF(): Functor<ConstPartialOf<Either<L, R>>> = Const.functor()

  override fun Either<L, R>.projectT(): Kind<ConstPartialOf<Either<L, R>>, Either<L, R>> = Const(this)
  override fun Kind<ConstPartialOf<Either<L, R>>, Eval<Either<L, R>>>.embedT(): Eval<Either<L, R>> = Eval.now(value())
}