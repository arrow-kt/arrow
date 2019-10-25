package arrow.fx.typeclasses

import arrow.Kind
import arrow.typeclasses.MonadContinuation
import arrow.typeclasses.MonadSyntax
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.RestrictsSuspension

@RestrictsSuspension
interface MonadDeferSyntax<F, E> : MonadSyntax<F>, MonadDefer<F, E>

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
open class MonadDeferContinuation<F, A, E>(ME: MonadDefer<F, E>, override val context: CoroutineContext = EmptyCoroutineContext) :
  MonadContinuation<F, A>(ME), MonadDefer<F, E> by ME, MonadDeferSyntax<F, E> {

  override val fx: MonadDeferFx<F, E> = ME.fx

  @Suppress("UNCHECKED_CAST")
  override fun resumeWithException(exception: Throwable) {
    returnedMonad = exception.raiseThrowableNonFatal()
  }

  override fun <B> binding(c: suspend MonadSyntax<F>.() -> B): Kind<F, B> = fx.monad(c)
}
