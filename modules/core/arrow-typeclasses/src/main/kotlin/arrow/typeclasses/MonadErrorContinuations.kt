package arrow.typeclasses

import arrow.typeclasses.suspended.MonadErrorSyntax
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.RestrictsSuspension

@RestrictsSuspension
@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
open class MonadErrorContinuation<F, A>(val ME: MonadError<F, Throwable>, override val context: CoroutineContext = EmptyCoroutineContext) :
  MonadContinuation<F, A>(ME), MonadError<F, Throwable> by ME, MonadErrorSyntax<F, Throwable> {

  override fun resumeWithException(exception: Throwable) {
    returnedMonad = ME.raiseError(exception)
  }
}