package arrow.effects.typeclasses

import arrow.Kind
import arrow.core.Either
import arrow.typeclasses.MonadContinuation
import arrow.typeclasses.MonadThrowContinuation
import arrow.typeclasses.stateStack
import arrow.typeclasses.suspended.BindSyntax
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.RestrictsSuspension
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.startCoroutine

typealias Disposable = () -> Unit

@RestrictsSuspension
@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
open class AsyncContinuation<F, A>(val SC: Async<F>, override val context: CoroutineContext = EmptyCoroutineContext) :
  MonadThrowContinuation<F, A>(SC), Async<F> by SC, BindSyntax<F> {
  override val fx: PartiallyAppliedAsyncFx<F> = SC.fx
}
