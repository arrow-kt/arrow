package kategory.effects

import kategory.*
import kategory.effects.data.internal.BindingCancellationException
import kategory.effects.internal.stackLabels
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.EmptyCoroutineContext
import kotlin.coroutines.experimental.RestrictsSuspension
import kotlin.coroutines.experimental.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.experimental.intrinsics.suspendCoroutineOrReturn
import kotlin.coroutines.experimental.startCoroutine

typealias Disposable = () -> Unit

@RestrictsSuspension
open class MonadErrorCancellableContinuation<F, A>(ME: MonadError<F, Throwable>, override val context: CoroutineContext = EmptyCoroutineContext) :
        MonadErrorContinuation<F, A>(ME) {

    protected val cancelled: AtomicBoolean = AtomicBoolean(false)

    fun disposable(): Disposable = { cancelled.set(true) }

    internal fun returnedMonad(): HK<F, A> = returnedMonad

    override suspend fun <B> bind(m: () -> HK<F, B>): B = suspendCoroutineOrReturn { c ->
        val labelHere = c.stackLabels // save the whole coroutine stack labels
        returnedMonad = flatMap(m(), { x: B ->
            c.stackLabels = labelHere
            if (cancelled.get()) {
                throw BindingCancellationException()
            }
            c.resume(x)
            returnedMonad
        })
        COROUTINE_SUSPENDED
    }

    override suspend fun <B> bindIn(context: CoroutineContext, m: () -> HK<F, B>): B = suspendCoroutineOrReturn { c ->
        val labelHere = c.stackLabels // save the whole coroutine stack labels
        val monadCreation: suspend () -> HK<F, A> = {
            flatMap(m(), { xx: B ->
                c.stackLabels = labelHere
                if (cancelled.get()) {
                    throw BindingCancellationException()
                }
                c.resume(xx)
                returnedMonad
            })
        }
        val completion = bindingInContextContinuation(context)
        returnedMonad = flatMap(pure(Unit), {
            monadCreation.startCoroutine(completion)
            val error = completion.await()
            if (error != null) {
                throw error
            }
            returnedMonad
        })
        COROUTINE_SUSPENDED
    }
}

/**
 * Entry point for monad bindings which enables for comprehensions. The underlying impl is based on coroutines.
 * A coroutines is initiated and inside [MonadErrorCancellableContinuation] suspended yielding to [Monad.flatMap]. Once all the flatMap binds are completed
 * the underlying monad is returned from the act of executing the coroutine
 *
 * This one operates over [MonadError] instances that can support [Throwable] in their error type automatically lifting
 * errors as failed computations in their monadic context and not letting exceptions thrown as the regular monad binding does.
 *
 * This operation is cancellable by calling invoke on the [Disposable] return.
 * If [Disposable.invoke] is called the binding result will become a lifted [BindingCancellationException].
 */
fun <F, B> MonadError<F, Throwable>.bindingECancellable(c: suspend MonadErrorCancellableContinuation<F, *>.() -> HK<F, B>): Tuple2<HK<F, B>, Disposable> {
    val continuation = MonadErrorCancellableContinuation<F, B>(this)
    c.startCoroutine(continuation, continuation)
    return continuation.returnedMonad() toT continuation.disposable()
}