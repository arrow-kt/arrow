package arrow.fx.typeclasses

import arrow.fx.IODeprecation
import arrow.typeclasses.MonadThrowSyntax
import arrow.typeclasses.MonadThrowContinuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.RestrictsSuspension

@Deprecated(IODeprecation)
typealias Disposable = () -> Unit

@RestrictsSuspension
@Deprecated(IODeprecation)
interface AsyncSyntax<F> : MonadThrowSyntax<F>, Async<F>

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
@Deprecated(IODeprecation)
open class AsyncContinuation<F, A>(val SC: Async<F>, override val context: CoroutineContext = EmptyCoroutineContext) :
  MonadThrowContinuation<F, A>(SC), Async<F> by SC, AsyncSyntax<F> {
  override val fx: AsyncFx<F> = SC.fx
}
