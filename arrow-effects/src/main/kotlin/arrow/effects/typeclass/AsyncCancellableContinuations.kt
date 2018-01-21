package arrow.effects

import arrow.HK
import arrow.core.Either
import arrow.core.Tuple2
import arrow.core.toT
import arrow.effects.data.internal.BindingCancellationException
import arrow.effects.internal.stackLabels
import arrow.typeclasses.MonadError
import arrow.typeclasses.MonadErrorContinuation
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.EmptyCoroutineContext
import kotlin.coroutines.experimental.RestrictsSuspension
import kotlin.coroutines.experimental.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.experimental.intrinsics.suspendCoroutineOrReturn
import kotlin.coroutines.experimental.startCoroutine

typealias Disposable = () -> Unit

@RestrictsSuspension
open class AsyncCancellableContinuation<F, A>(val AC: Async<F>, override val context: CoroutineContext = EmptyCoroutineContext) :
        MonadErrorContinuation<F, A>(AC) {

    protected val cancelled: AtomicBoolean = AtomicBoolean(false)

    fun disposable(): Disposable = { cancelled.set(true) }

    override fun returnedMonad(): HK<F, A> = returnedMonad

    suspend fun <B> bindAsync(f: () -> B): B =
            AC(f).bind()

    suspend fun <B> bindAsyncUnsafe(f: () -> Either<Throwable, B>): B =
            AC.deferUnsafe(f).bind()

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

    override suspend fun <B> bindIn(context: CoroutineContext, m: () -> B): B = suspendCoroutineOrReturn { c ->
        val labelHere = c.stackLabels // save the whole coroutine stack labels
        val monadCreation: suspend () -> HK<F, A> = {
            val datatype = try {
                pure(m())
            } catch (t: Throwable) {
                ME.raiseError<B>(t)
            }
            flatMap(datatype, { xx: B ->
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
