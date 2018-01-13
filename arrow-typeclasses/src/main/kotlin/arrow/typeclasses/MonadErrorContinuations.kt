package arrow.typeclasses

import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.EmptyCoroutineContext
import kotlin.coroutines.experimental.RestrictsSuspension

@RestrictsSuspension
open class MonadErrorContinuation<F, A>(val ME: MonadError<F, Throwable>, override val context: CoroutineContext = EmptyCoroutineContext) :
        MonadContinuation<F, A>(ME) {

    override fun resumeWithException(exception: Throwable) {
        returnedMonad = ME.raiseError(exception)
    }
}