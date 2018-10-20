package arrow.typeclasses

import arrow.Kind
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.RestrictsSuspension

@RestrictsSuspension
@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
open class MonadErrorContinuation<F, A>(val ME: MonadError<F, Throwable>, override val context: CoroutineContext = EmptyCoroutineContext) :
  MonadContinuation<F, A>(ME), MonadError<F, Throwable> by ME {

  override fun resumeWith(result: Result<Kind<F, A>>) =
    result.fold({ Unit }, {
      returnedMonad = ME.raiseError(it)
    })

}