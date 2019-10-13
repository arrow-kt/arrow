package arrow.typeclasses

import arrow.Kind
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.RestrictsSuspension

@RestrictsSuspension
interface MonadErrorSyntax<F, E> : MonadSyntax<F>, MonadError<F, E>

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
open class MonadErrorContinuation<F, A, E>(ME: MonadError<F, E>, override val context: CoroutineContext = EmptyCoroutineContext, val fe: (Throwable) -> E) :
  MonadContinuation<F, A>(ME), MonadError<F, E> by ME, MonadErrorSyntax<F, E> {

  override val fx: MonadErrorFx<F, E> = ME.fx

  @Suppress("UNCHECKED_CAST")
  override fun resumeWithException(exception: Throwable) {
    returnedMonad = raiseError(fe(exception))
  }

  override fun <B> binding(c: suspend MonadSyntax<F>.() -> B): Kind<F, B> = fx.monad(c)
}
