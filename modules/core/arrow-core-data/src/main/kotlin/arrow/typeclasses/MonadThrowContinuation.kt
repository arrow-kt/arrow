package arrow.typeclasses

import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.RestrictsSuspension

@RestrictsSuspension
interface MonadThrowSyntax<F> : MonadSyntax<F>, MonadThrow<F>

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
open class MonadThrowContinuation<F, A>(ME: MonadThrow<F>, override val context: CoroutineContext = EmptyCoroutineContext) :
  MonadContinuation<F, A>(ME), MonadThrow<F> by ME, MonadThrowSyntax<F> {

  override val fx: MonadThrowFx<F> = ME.fx

  @Suppress("UNCHECKED_CAST")
  override fun resumeWithException(exception: Throwable) {
    returnedMonad = raiseError(exception)
  }
}
