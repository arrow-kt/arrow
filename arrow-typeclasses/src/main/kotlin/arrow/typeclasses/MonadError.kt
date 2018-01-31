package arrow.typeclasses

import arrow.HK
import arrow.TC
import arrow.typeclass
import kotlin.coroutines.experimental.startCoroutine

@typeclass
interface MonadError<F, E> : ApplicativeError<F, E>, Monad<F>, TC {

    fun <A> ensure(fa: HK<F, A>, error: () -> E, predicate: (A) -> Boolean): HK<F, A> =
            flatMap(fa, {
                if (predicate(it)) pure(it)
                else raiseError(error())
            })

}

/**
 * Entry point for monad bindings which enables for comprehensions. The underlying implementation is based on coroutines.
 * A coroutine is initiated and suspended inside [MonadErrorContinuation] yielding to [Monad.flatMap]. Once all the flatMap binds are completed
 * the underlying monad is returned from the act of executing the coroutine
 *
 * This one operates over [MonadError] instances that can support [Throwable] in their error type automatically lifting
 * errors as failed computations in their monadic context and not letting exceptions thrown as the regular monad binding does.
 */
fun <F, B> MonadError<F, Throwable>.bindingCatch(c: suspend MonadErrorContinuation<F, *>.() -> B): HK<F, B> {
    val continuation = MonadErrorContinuation<F, B>(this)
    val wrapReturn: suspend MonadErrorContinuation<F, *>.() -> HK<F, B> = { pure(c()) }
    wrapReturn.startCoroutine(continuation, continuation)
    return continuation.returnedMonad()
}
