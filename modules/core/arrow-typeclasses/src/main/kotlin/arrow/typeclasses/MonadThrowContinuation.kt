package arrow.typeclasses

import arrow.Kind
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.RestrictsSuspension

@RestrictsSuspension
@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
open class MonadThrowContinuation<F, A>(ME: MonadThrow<F>, override val context: CoroutineContext = EmptyCoroutineContext) :
  MonadContinuation<F, A>(ME), MonadThrow<F> by ME {

  override val fx: PartiallyAppliedMonadThrowFx<F> = ME.fx

  @Suppress("UNCHECKED_CAST")
  override fun resumeWithException(exception: Throwable) {
    returnedMonad = when (exception) {
      is ContinuationShortcircuitThrowable -> exception.exit as Kind<F, A>
      else -> raiseError(exception)
    }
  }

  override fun <B> binding(c: suspend MonadContinuation<F, *>.() -> B): Kind<F, B> = fx.monad(c)
}
