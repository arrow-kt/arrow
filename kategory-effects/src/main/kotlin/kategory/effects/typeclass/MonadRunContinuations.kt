package kategory.effects

import kategory.*
import kategory.effects.data.internal.BindingCancellationException
import kategory.effects.internal.stackLabels
import kotlin.coroutines.experimental.*
import kotlin.coroutines.experimental.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.experimental.intrinsics.suspendCoroutineOrReturn

data class Fiber<out F, out A>(val binding: HK<F, A>, private val dispose: Disposable) : Disposable by dispose

@RestrictsSuspension
open class MonadRunContinuation<F, A>(val MR: MonadRun<F, Throwable>, AC: AsyncContext<F>, override val context: CoroutineContext = EmptyCoroutineContext) :
        MonadErrorCancellableContinuation<F, A>(MR), MonadRun<F, Throwable> by MR, AsyncContext<F> by AC {

    open suspend fun <B, C> bindParallel(cc: CoroutineContext, fa: HK<F, B>, fb: HK<F, C>): Tuple2<B, C> = suspendCoroutineOrReturn { c ->
        currentCont = c
        val labelHere = c.stackLabels
        returnedMonad = flatMap(pure(Unit)) {
            val c1: BindingInContextContinuation<F, B> = bindParallelContinuation(fa, cc)
            val c2: BindingInContextContinuation<F, C> = bindParallelContinuation(fb, cc)

            bindingE {
                c1.start().bind()
                c2.start().bind()
                val resultB = c1.await().bind()
                val resultC = c2.await().bind()
                c.stackLabels = labelHere
                c.resume(resultB toT resultC)
                returnedMonad
            }
        }
        COROUTINE_SUSPENDED
    }

    open suspend fun <B, C> bindRace(cc: CoroutineContext, fa: Fiber<F, B>, fb: Fiber<F, C>): Either<B, C> = suspendCoroutineOrReturn { c ->
        currentCont = c
        val labelHere = c.stackLabels
        returnedMonad = flatMap(pure(Unit)) {
            val c1: BindingInContextContinuation<F, B> = bindParallelContinuation(raceWith(fa.binding, { fb() }), cc)
            val c2: BindingInContextContinuation<F, C> = bindParallelContinuation(raceWith(fb.binding, { fb() }), cc)

            bindingE {
                c1.start().bind()
                c2.start().bind()
                val resultB = recoverCancellation(c1.await()).bind()
                val resultC = recoverCancellation(c2.await()).bind()
                val result: Either<B, C> = resultC.fold(
                        {
                            resultB.fold(
                                    { ME.raiseError<Either<B, C>>(BindingCancellationException("All operations were cancelled")) },
                                    { pure(it.left()) })
                        },
                        { pure(it.right()) }
                ).bind()
                c.stackLabels = labelHere
                c.resume(result)
                returnedMonad
            }
        }
        COROUTINE_SUSPENDED
    }

    protected fun <B> bindParallelContinuation(fb: HK<F, B>, context: CoroutineContext): BindingInContextContinuation<F, B> =
            object : BindingInContextContinuation<F, B> {
                override fun start() = runAsync<Unit> { it(suspendUnsafeRunAsync(fb).startCoroutine(this).right()) }

                var callback: ((Either<Throwable, B>) -> Unit)? = null

                var eager: Either<Throwable, B>? = null

                override fun await(): HK<F, B> = runAsync { cb: (Either<Throwable, B>) -> Unit ->
                    callback = cb
                    val localEager = eager
                    if (localEager != null) {
                        cb(localEager)
                    }
                }

                override val context: CoroutineContext = context

                override fun resume(value: B) {
                    val localCallback = callback
                    if (localCallback == null) {
                        eager = value.right()
                    } else {
                        localCallback(value.right())
                    }
                }

                override fun resumeWithException(exception: Throwable) {
                    val localCallback = callback
                    if (localCallback == null) {
                        eager = exception.left()
                    } else {
                        localCallback(exception.left())
                    }
                }
            }

    private fun <B> suspendUnsafeRunAsync(a: HK<F, B>): suspend () -> B = {
        suspendCoroutineOrReturn { cc ->
            unsafeRunAsync(a, { cb ->
                cb.fold({ cc.resumeWithException(it) }, { cc.resume(it) })
            })
            COROUTINE_SUSPENDED
        }
    }

    private fun <B> raceWith(fa: HK<F, B>, dispose: Disposable): HK<F, B> =
            map(fa) { dispose(); it }

    private fun <B> recoverCancellation(await: HK<F, B>): HK<F, Option<B>> =
            handleErrorWith(map(await) { it.some() }) {
                if (it is BindingCancellationException) {
                    pure(Option.empty())
                } else {
                    raiseError(it)
                }
            }

    protected interface BindingInContextContinuation<out F, A> : Continuation<A> {
        fun start(): HK<F, Unit>

        fun await(): HK<F, A>
    }
}

/**
 * Entry point for monad bindings which enables for comprehensions. The underlying impl is based on coroutines.
 * A coroutines is initiated and inside [MonadRunContinuation] suspended yielding to [Monad.flatMap]. Once all the flatMap binds are completed
 * the underlying monad is returned from the act of executing the coroutine
 *
 * This one operates over [MonadRun] instances that can support [Throwable] in their error type automatically lifting
 * errors as failed computations in their monadic context and not letting exceptions thrown as the regular monad binding does.
 *
 * This operation is cancellable by calling invoke on the [Fiber] return or its [Disposable].
 * If [invoke] is called the binding result will become a lifted [BindingCancellationException].
 *
 * This operation is cancellable by calling invoke on the [Disposable] return.
 * If [Disposable.invoke] is called the binding result will become a lifted [BindingCancellationException].
 *
 */
fun <F, B> MonadRun<F, Throwable>.bindingFiber(AC: AsyncContext<F>, c: suspend MonadRunContinuation<F, *>.() -> HK<F, B>): Fiber<F, B> {
    val continuation = MonadRunContinuation<F, B>(this, AC)
    c.startCoroutine(continuation, continuation)
    return Fiber(continuation.returnedMonad(), continuation.disposable())
}