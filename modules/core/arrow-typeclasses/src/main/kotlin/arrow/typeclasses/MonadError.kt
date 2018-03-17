package arrow.typeclasses

import arrow.Kind
import arrow.TC
import arrow.core.Either
import arrow.core.identity
import arrow.typeclass
import arrow.typeclasses.continuations.BindingCatchContinuation
import arrow.typeclasses.continuations.MonadErrorBlockingContinuation
import arrow.typeclasses.internal.Platform
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
     * Entry point for monad bindings which enables for comprehension. The underlying implementation is based on coroutines.
     * A coroutine is initiated and suspended inside [BindingCatchContinuation] yielding to [Monad.flatMap] or [Monad.flatMapIn].
     * Once all the binds are completed the underlying data type is returned from the act of executing the coroutine.
     *
     * These for comprehensions enable working for any error [E] as long as a conversion function from any [Throwable].
     */
    fun <B> bindingCatch(catch: (Throwable) -> E, c: suspend BindingCatchContinuation<F, E, *>.() -> B): Kind<F, B> {
        val continuation = MonadErrorBlockingContinuation<F, E, B>(this, Platform.awaitableLatch(), EmptyCoroutineContext, catch)
        val coro: suspend () -> Kind<F, B> = { pure(c(continuation)).also { continuation.resolve(Either.right(it)) } }
        coro.startCoroutine(continuation)
        return continuation.returnedMonad()
    }
}

fun <F, B> MonadError<F, Throwable>.bindingCatch(c: suspend BindingCatchContinuation<F, Throwable, *>.() -> B): Kind<F, B> =
        bindingCatch(::identity, c)
