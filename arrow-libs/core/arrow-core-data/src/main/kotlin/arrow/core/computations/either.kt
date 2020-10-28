package arrow.core.computations

import arrow.Kind
import arrow.continuations.generic.DelimContScope
import arrow.core.EagerBind
import arrow.core.Either
import arrow.core.EitherPartialOf
import arrow.core.fix
import arrow.typeclasses.suspended.BindSyntax

object either {

  fun <E, A> eager(c: suspend EagerBind<EitherPartialOf<E>>.() -> A): Either<E, A> =
    DelimContScope.reset {
      Either.Right(
        c(object : EagerBind<EitherPartialOf<E>> {
          override suspend fun <A> Kind<EitherPartialOf<E>, A>.invoke(): A =
            when (val v = fix()) {
              is Either.Right -> v.b
              is Either.Left -> shift { v }
            }
        })
      )
    }

  suspend operator fun <E, A> invoke(c: suspend BindSyntax<EitherPartialOf<E>>.() -> A): Either<E, A> =
    DelimContScope.reset {
      Either.Right(
        c(object : BindSyntax<EitherPartialOf<E>> {
          override suspend fun <A> Kind<EitherPartialOf<E>, A>.invoke(): A =
            when (val v = fix()) {
              is Either.Right -> v.b
              is Either.Left -> shift { v }
            }
        })
      )
    }
}
