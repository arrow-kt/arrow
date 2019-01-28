package arrow.typeclasses

import arrow.Kind
import arrow.typeclasses.suspended.MonadThrowSyntax
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.RestrictsSuspension

@RestrictsSuspension
@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
open class MonadErrorContinuation<F, A>(ME: MonadError<F, Throwable>, override val context: CoroutineContext = EmptyCoroutineContext) :
  MonadContinuation<F, A>(ME), MonadError<F, Throwable> by ME, MonadThrowSyntax<F> {

  override fun resumeWithException(exception: Throwable) {
    returnedMonad = raiseError(exception)
  }

  override fun <A> fx(f: suspend MonadContinuation<F, *>.() -> A): Kind<F, A> =
    bindingCatch(f)

  override fun <B> binding(c: suspend MonadContinuation<F, *>.() -> B): Kind<F, B> =
    bindingCatch(c)

}