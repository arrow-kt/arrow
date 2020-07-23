package arrow.core.computations

import arrow.Kind
import arrow.core.EagerBind
import arrow.core.Eval
import arrow.core.EvalOf
import arrow.core.ForEval
import arrow.core.MonadContinuation
import arrow.core.ShortCircuit
import arrow.core.SuspendMonadContinuation
import arrow.core.fix
import arrow.typeclasses.suspended.BindSyntax
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn

object eval {
  fun <A> eager(c: suspend EagerBind<ForEval>.() -> A): Eval<A> {
    val continuation: EvalContinuation<A> = EvalContinuation()
    return continuation.startCoroutineUninterceptedAndReturn {
      Eval.just(c())
    } as Eval<A>
  }

  suspend operator fun <A> invoke(c: suspend BindSyntax<ForEval>.() -> A): Eval<A> =
    suspendCoroutineUninterceptedOrReturn { cont ->
      val continuation = EvalSContinuation(cont as Continuation<EvalOf<A>>)
      continuation.startCoroutineUninterceptedOrReturn {
        Eval.just(c())
      }
    }

  internal class EvalSContinuation<A>(
    parent: Continuation<EvalOf<A>>
  ) : SuspendMonadContinuation<ForEval, A>(parent) {
    override fun ShortCircuit.recover(): Kind<ForEval, A> =
      throw this

    override suspend fun <A> Kind<ForEval, A>.bind(): A =
      fix().value()
  }

  internal class EvalContinuation<A> : MonadContinuation<ForEval, A>() {
    override fun ShortCircuit.recover(): Kind<ForEval, A> =
      throw this

    override suspend fun <A> Kind<ForEval, A>.bind(): A =
      fix().value()
  }
}
