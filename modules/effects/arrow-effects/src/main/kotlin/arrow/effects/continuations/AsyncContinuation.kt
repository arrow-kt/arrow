package arrow.effects.continuations

import arrow.Kind
import arrow.effects.Async
import arrow.typeclasses.stackLabels
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.EmptyCoroutineContext
import kotlin.coroutines.experimental.startCoroutine
import kotlin.coroutines.experimental.suspendCoroutine

class AsyncContinuation<F, E, A>(AS: Async<F, E>, val catchF: (Throwable) -> E, val bindInContext: CoroutineContext) :
        BindingAsyncContinuation<F, E, A>, Async<F, E> by AS {
    override val context: CoroutineContext = EmptyCoroutineContext

    override fun resume(value: Kind<F, A>) {
        returnedMonad = value
    }

    override fun resumeWithException(exception: Throwable) {
        returnedMonad = raiseError(catchF(exception))
    }

    protected lateinit var returnedMonad: Kind<F, A>

    fun returnedMonad(): Kind<F, A> = returnedMonad

    override suspend fun <B> bind(m: () -> Kind<F, B>): B = suspendCoroutine { c ->
        val labelHere = c.stackLabels // save the whole coroutine stack labels
        returnedMonad = flatMapIn(m(), bindInContext, { x: B ->
            c.stackLabels = labelHere
            c.resume(x)
            returnedMonad
        })
    }

    companion object {
        fun <F, E, A> binding(catch: (Throwable) -> E, AS: Async<F, E>, cc: CoroutineContext, c: suspend BindingAsyncContinuation<F, E, *>.() -> A): Kind<F, A> =
                AS.flatMapIn(AS.invoke {}, cc) {
                    val continuation = AsyncContinuation<F, E, A>(AS, catch, cc)
                    val coro: suspend () -> Kind<F, A> = { AS.pure(c(continuation)) }
                    coro.startCoroutine(continuation)
                    continuation.returnedMonad()
                }
    }
}
