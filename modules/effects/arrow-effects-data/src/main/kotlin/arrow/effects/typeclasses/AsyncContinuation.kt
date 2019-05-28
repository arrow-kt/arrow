package arrow.effects.typeclasses

import arrow.typeclasses.MonadThrowContinuation
import arrow.typeclasses.suspended.BindSyntax
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.RestrictsSuspension

typealias Disposable = () -> Unit

@RestrictsSuspension
@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
open class AsyncContinuation<F, A>(val SC: Async<F>, override val context: CoroutineContext = EmptyCoroutineContext) :
  MonadThrowContinuation<F, A>(SC), Async<F> by SC, BindSyntax<F> {
  override val fx: AsyncFx<F> = SC.fx
}
