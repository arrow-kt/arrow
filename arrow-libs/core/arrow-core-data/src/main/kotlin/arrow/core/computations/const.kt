package arrow.core.computations

import arrow.Kind
import arrow.continuations.generic.DelimContScope
import arrow.core.Const
import arrow.core.ConstPartialOf
import arrow.core.EagerBind
import arrow.core.const
import arrow.core.fix
import arrow.typeclasses.suspended.BindSyntax

object const {

  fun <A, T> eager(c: suspend EagerBind<ConstPartialOf<A>>.() -> A): Const<A, T> =
    DelimContScope.reset {
      c(object : EagerBind<ConstPartialOf<A>> {
        override suspend fun <T> Kind<ConstPartialOf<A>, T>.invoke(): T =
          fix().value() as T
      }).const()
    }

  suspend operator fun <A, T> invoke(c: suspend BindSyntax<ConstPartialOf<A>>.() -> A): Const<A, T> =
    DelimContScope.reset {
      c(object : BindSyntax<ConstPartialOf<A>> {
        override suspend fun <T> Kind<ConstPartialOf<A>, T>.invoke(): T =
          fix().value() as T
      }).const()
    }
}
