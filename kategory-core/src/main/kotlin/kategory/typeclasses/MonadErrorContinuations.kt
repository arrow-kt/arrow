package kategory

import kotlin.coroutines.experimental.Continuation
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.EmptyCoroutineContext
import kotlin.coroutines.experimental.RestrictsSuspension

@RestrictsSuspension
open class MonadErrorContinuation<F, A>(val ME: MonadError<F, Throwable>, override val context: CoroutineContext = EmptyCoroutineContext) :
        MonadContinuation<F, A>(ME), MonadError<F, Throwable> by ME {

    override fun bindingInContextContinuation(context: CoroutineContext): Continuation<HK<F, A>> =
            object : Continuation<HK<F, A>> {
                override val context: CoroutineContext = context

                override fun resume(value: HK<F, A>) {
                    returnedMonad = value
                }

                override fun resumeWithException(exception: Throwable) {
                    returnedMonad = raiseError(exception)
                }
            }

    override fun resumeWithException(exception: Throwable) {
        returnedMonad = ME.raiseError(exception)
    }
}