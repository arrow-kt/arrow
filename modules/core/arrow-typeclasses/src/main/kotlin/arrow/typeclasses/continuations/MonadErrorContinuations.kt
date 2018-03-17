package arrow.typeclasses.continuations

import arrow.Kind
import arrow.typeclasses.Awaitable
import arrow.typeclasses.MonadError
import arrow.typeclasses.internal.Platform
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.startCoroutine

open class MonadErrorBlockingContinuation<F, E, A>(ME: MonadError<F, E>, latch: Awaitable<Kind<F, A>>, override val context: CoroutineContext, private val convertError: (Throwable) -> E) :
        MonadBlockingContinuation<F, A>(ME, latch, context), MonadError<F, E> by ME, BindingCatchContinuation<F, E, A> {

    override fun returnedMonad(): Kind<F, A> =
            awaitBlocking().fold({ raiseError(convertError(it)) }, { result -> flatMap(returnedMonad, { result }) })

    companion object {
        fun <F, E, B> bindingCatch(ME: MonadError<F, E>, catch: (Throwable) -> E, context: CoroutineContext, c: suspend (BindingCatchContinuation<F, E, *>) -> Kind<F, B>): Kind<F, B> {
            val continuation = MonadErrorBlockingContinuation<F, E, B>(ME, Platform.awaitableLatch(), context, catch)
            c.startCoroutine(continuation, continuation)
            return continuation.returnedMonad()
        }
    }
}
