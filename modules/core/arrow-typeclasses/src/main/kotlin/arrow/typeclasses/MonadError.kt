package arrow.typeclasses

import arrow.Kind
import arrow.TC
import arrow.core.Either
import arrow.core.identity
import arrow.typeclass
import arrow.typeclasses.continuations.MonadErrorBlockingContinuation
import arrow.typeclasses.internal.Platform
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.EmptyCoroutineContext
import kotlin.coroutines.experimental.startCoroutine

@typeclass
interface MonadError<F, E> : ApplicativeError<F, E>, Monad<F>, TC {

    fun <A> ensure(fa: Kind<F, A>, error: () -> E, predicate: (A) -> Boolean): Kind<F, A> =
            flatMap(fa, {
                if (predicate(it)) pure(it)
                else raiseError(error())
            })

    /**
     * Entry point for monad bindings which enables for comprehensions. The underlying implementation is based on coroutines.
     * A coroutine is initiated and suspended inside [MonadErrorBlockingContinuation] yielding to [Monad.flatMap]. Once all the flatMap binds are completed
     * the underlying monad is returned from the act of executing the coroutine
     *
     * This one operates over [MonadError] instances that can support [Throwable] in their error type automatically lifting
     * errors as failed computations in their monadic context and not letting exceptions thrown as the regular monad binding does.
     */
    fun <B> bindingCatch(cc: CoroutineContext = EmptyCoroutineContext, catch: (Throwable) -> E, c: suspend MonadErrorBlockingContinuation<F, E, *>.() -> B): Kind<F, B> {
        val continuation = MonadErrorBlockingContinuation<F, E, B>(this, Platform.awaitableLatch(), cc, catch)
        val coro: suspend () -> Kind<F, B> = { pure(c(continuation)).also { continuation.resolve(Either.right(it)) } }
        coro.startCoroutine(continuation)
        return continuation.returnedMonad()
    }
}

fun <F, B>  MonadError<F, Throwable>.bindingCatch(cc: CoroutineContext = EmptyCoroutineContext, c: suspend MonadErrorBlockingContinuation<F, Throwable, *>.() -> B): Kind<F, B> =
        bindingCatch(cc, ::identity, c)
