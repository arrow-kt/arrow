package arrow.core.computations

import arrow.Kind
import arrow.core.Const
import arrow.core.ConstOf
import arrow.core.ConstPartialOf
import arrow.core.EagerBind
import arrow.core.MonadContinuation
import arrow.core.ShortCircuit
import arrow.core.SuspendMonadContinuation
import arrow.core.value
import arrow.typeclasses.suspended.BindSyntax
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn

object const {
  fun <A, T> eager(c: suspend EagerBind<ConstPartialOf<A>>.() -> A): Const<A, T> {
    val continuation: ConstContinuation<A, A> = ConstContinuation()
    return continuation.startCoroutineUninterceptedAndReturn {
      Const.just(c())
    } as Const<A, T>
  }

  suspend operator fun <A, T> invoke(c: suspend BindSyntax<ConstPartialOf<A>>.() -> A): Const<A, T> =
    suspendCoroutineUninterceptedOrReturn { cont ->
      val continuation = ConstSContinuation(cont as Continuation<ConstOf<A, T>>)
      continuation.startCoroutineUninterceptedOrReturn {
        Const.just(c())
      }
    }

  internal class ConstSContinuation<A, T>(
    parent: Continuation<ConstOf<A, T>>
  ) : SuspendMonadContinuation<ConstPartialOf<A>, T>(parent) {
    override fun ShortCircuit.recover(): Const<A, T> =
      throw this

    override suspend fun <B> Kind<ConstPartialOf<A>, B>.bind(): B =
      value() as B
  }

  internal class ConstContinuation<A, T> : MonadContinuation<ConstPartialOf<A>, T>() {
    override fun ShortCircuit.recover(): Const<A, T> =
      throw this

    override suspend fun <B> Kind<ConstPartialOf<A>, B>.bind(): B =
      value() as B
  }
}
