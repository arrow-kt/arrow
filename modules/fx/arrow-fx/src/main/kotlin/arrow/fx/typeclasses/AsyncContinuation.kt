package arrow.fx.typeclasses

import arrow.typeclasses.MonadErrorContinuation
import arrow.typeclasses.MonadErrorSyntax
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.RestrictsSuspension

typealias Disposable = () -> Unit

@RestrictsSuspension
interface AsyncSyntax<F, E> : MonadErrorSyntax<F, E>, Async<F, E>

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
open class AsyncContinuation<F, A, E>(val SC: Async<F, E>, fe: (Throwable) -> E, override val context: CoroutineContext = EmptyCoroutineContext) :
  MonadErrorContinuation<F, A, E>(SC, fe = fe), Async<F, E> by SC, AsyncSyntax<F, E> {
  override val fx: AsyncFx<F, E> = SC.fx
}
