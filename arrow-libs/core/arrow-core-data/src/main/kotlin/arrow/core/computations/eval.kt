package arrow.core.computations

import arrow.Kind
import arrow.continuations.generic.DelimContScope
import arrow.core.EagerBind
import arrow.core.Eval
import arrow.core.ForEval
import arrow.core.fix
import arrow.typeclasses.suspended.BindSyntax

object eval {

  fun <A> eager(c: suspend EagerBind<ForEval>.() -> A): Eval<A> =
    DelimContScope.reset {
      Eval.just(
        c(object : EagerBind<ForEval> {
          override suspend fun <A> Kind<ForEval, A>.invoke(): A =
            fix().value()
        })
      )
    }

  suspend operator fun <A> invoke(c: suspend BindSyntax<ForEval>.() -> A): Eval<A> =
    DelimContScope.reset {
      Eval.just(
        c(object : BindSyntax<ForEval> {
          override suspend fun <A> Kind<ForEval, A>.invoke(): A =
            fix().value()
        })
      )
    }
}
