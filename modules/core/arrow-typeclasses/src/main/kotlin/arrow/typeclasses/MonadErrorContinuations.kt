package arrow.typeclasses

import arrow.Kind
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.RestrictsSuspension

@RestrictsSuspension
@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
open class MonadErrorContinuation<F, A>(ME: MonadThrow<F>, override val context: CoroutineContext = EmptyCoroutineContext) :
  MonadContinuation<F, A>(ME), MonadThrow<F> by ME {

  override fun resumeWithException(exception: Throwable) {
    returnedMonad = raiseError(exception)
  }

  override fun <B> binding(c: suspend MonadContinuation<F, *>.() -> B): Kind<F, B> =
    bindingCatch(c)

}