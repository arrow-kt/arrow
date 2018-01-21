package arrow.effects

import arrow.HK
import arrow.TC
import arrow.core.Either
import arrow.core.Tuple2
import arrow.core.toT
import arrow.effects.data.internal.BindingCancellationException
import arrow.typeclass
import arrow.typeclasses.MonadError
import kotlin.coroutines.experimental.startCoroutine

        /** An asynchronous computation that might fail. **/
typealias Proc<A> = ((Either<Throwable, A>) -> Unit) -> Unit

/** The context required to run an asynchronous computation that may fail. **/
@typeclass
interface Async<F> : Sync<F>, TC {
    fun <A> async(fa: Proc<A>): HK<F, A>

    fun <A> never(): HK<F, A> =
            async { }
}

/**
 * Entry point for monad bindings which enables for comprehensions. The underlying impl is based on coroutines.
 * A coroutines is initiated and inside [AsyncCancellableContinuation] suspended yielding to [Monad.flatMap]. Once all the flatMap binds are completed
 * the underlying monad is returned from the act of executing the coroutine
 *
 * This one operates over [MonadError] instances that can support [Throwable] in their error type automatically lifting
 * errors as failed computations in their monadic context and not letting exceptions thrown as the regular monad binding does.
 *
 * This operation is cancellable by calling invoke on the [Disposable] return.
 * If [Disposable.invoke] is called the binding result will become a lifted [BindingCancellationException].
 */
fun <F, B> Async<F>.bindingCancellable(c: suspend AsyncCancellableContinuation<F, *>.() -> HK<F, B>): Tuple2<HK<F, B>, Disposable> {
    val continuation = AsyncCancellableContinuation<F, B>(this)
    c.startCoroutine(continuation, continuation)
    return continuation.returnedMonad() toT continuation.disposable()
}
