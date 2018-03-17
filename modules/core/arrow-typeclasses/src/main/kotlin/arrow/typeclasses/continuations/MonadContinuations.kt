package arrow.typeclasses.continuations

import arrow.Kind
import arrow.core.Either
import arrow.typeclasses.Awaitable
import arrow.typeclasses.Monad
import arrow.typeclasses.internal.Platform
import arrow.typeclasses.stackLabels
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.EmptyCoroutineContext
import kotlin.coroutines.experimental.startCoroutine
import kotlin.coroutines.experimental.suspendCoroutine

open class MonadBlockingContinuation<F, A>(M: Monad<F>, latch: Awaitable<Kind<F, A>>, override val context: CoroutineContext) :
        Monad<F> by M, Awaitable<Kind<F, A>> by latch, BindingContinuation<F, A> {

    override fun resume(value: Kind<F, A>) {
        resolve(Either.right(value))
    }

    override fun resumeWithException(exception: Throwable) {
        resolve(Either.left(exception))
    }

    protected var returnedMonad: Kind<F, Unit> = M.pure(Unit)

    open fun returnedMonad(): Kind<F, A> =
            awaitBlocking().fold({ throw it }, { it })

    override suspend fun <B> bind(m: () -> Kind<F, B>): B = suspendCoroutine { c ->
        val labelHere = c.stackLabels // save the whole coroutine stack labels
        returnedMonad = flatMap(m(), { x: B ->
            c.stackLabels = labelHere
            c.resume(x)
            returnedMonad
        })
    }

    companion object {
        fun <F, B> binding(M: Monad<F>, context: CoroutineContext = EmptyCoroutineContext, c: suspend (BindingContinuation<F, *>) -> Kind<F, B>): Kind<F, B> {
            val continuation = MonadBlockingContinuation<F, B>(M, Platform.awaitableLatch(), context)
            c.startCoroutine(continuation, continuation)
            return continuation.returnedMonad()
        }
    }
}
