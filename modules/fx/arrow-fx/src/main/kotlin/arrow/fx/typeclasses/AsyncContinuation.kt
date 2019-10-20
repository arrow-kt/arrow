package arrow.fx.typeclasses

import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.RestrictsSuspension

typealias Disposable = () -> Unit

@RestrictsSuspension
interface AsyncSyntax<F, E> : MonadDeferSyntax<F, E>, Async<F, E>

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
open class AsyncContinuation<F, A, E>(val SC: Async<F, E>, override val context: CoroutineContext = EmptyCoroutineContext) :
  MonadDeferContinuation<F, A, E>(SC), Async<F, E> by SC, AsyncSyntax<F, E> {
  override val fx: AsyncFx<F, E> = SC.fx
}
