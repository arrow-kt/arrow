package kategory.effects.typeclass

import kategory.*
import kategory.effects.Disposable
import kategory.effects.MonadDisposable
import kategory.effects.MonadErrorCancellableContinuation
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.EmptyCoroutineContext
import kotlin.coroutines.experimental.RestrictsSuspension
import kotlin.coroutines.experimental.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.experimental.intrinsics.suspendCoroutineOrReturn
import kotlin.coroutines.experimental.startCoroutine

@RestrictsSuspension
class MonadDisposableContinuation<F, A>(val DA: MonadDisposable<F, Throwable>, override val context: CoroutineContext = EmptyCoroutineContext) :
        MonadErrorCancellableContinuation<F, A>(DA) {

    override fun resumeWithException(exception: Throwable) {
        returnedMonad = ME.raiseError(exception)
    }

    override suspend fun <B> bind(m: () -> HK<F, B>): B = suspendCoroutineOrReturn { c ->
        val labelHere = c.stackLabels // save the whole coroutine stack labels
        returnedMonad = flatMap(m(), { x: B ->
            c.stackLabels = labelHere
            if (cancelled.get()) {
                DA.dispose(returnedMonad)
            } else {
                c.resume(x)
            }
            returnedMonad
        })
        COROUTINE_SUSPENDED
    }
}

/**
 * Entry point for monad bindings which enables for comprehensions. The underlying impl is based on coroutines.
 * A coroutines is initiated and inside `MonadErrorContinuation` suspended yielding to `flatMap` once all the flatMap binds are completed
 * the underlying monad is returned from the act of executing the coroutine
 *
 * This one operates over MonadError instances that can support `Throwable` in their error type automatically lifting
 * errors as failed computations in their monadic context and not letting exceptions thrown as the regular monad binding does.
 *
 * This operation is cancellable by calling invoke on the [Disposable] return.
 */
fun <F, B> MonadDisposable<F, Throwable>.bindingEDisposable(c: suspend MonadDisposableContinuation<F, *>.() -> HK<F, B>): Tuple2<HK<F, B>, Disposable> {
    val continuation = MonadDisposableContinuation<F, B>(this)
    c.startCoroutine(continuation, continuation)
    return continuation.returnedMonad() toT continuation.disposable()
}