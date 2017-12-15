package kategory.effects

import kategory.HK
import kategory.Monad
import kategory.Tuple2
import kategory.effects.data.internal.BindingCancellationException
import kategory.effects.internal.stackLabels
import kategory.tupled
import java.util.concurrent.CountDownLatch
import kotlin.coroutines.experimental.*
import kotlin.coroutines.experimental.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.experimental.intrinsics.suspendCoroutineOrReturn

data class Fiber<out F, out A>(val binding: HK<F, A>, private val dispose: Disposable) : Disposable by dispose

@RestrictsSuspension
open class MonadRunContinuation<F, A>(val MR: MonadRun<F, Throwable>, override val context: CoroutineContext = EmptyCoroutineContext) :
        MonadErrorCancellableContinuation<F, A>(MR) {

    open suspend fun <B, C> bindParallel(cc: CoroutineContext, fa: HK<F, B>, fb: HK<F, C>): Tuple2<B, C> = suspendCoroutineOrReturn { c ->
        val labelHere = c.stackLabels
        returnedMonad = flatMap(pure(Unit)) {
            val c1: BindingInContextContinuation<F, B> = bindParallelContinuation(fa, cc)
            val c2: BindingInContextContinuation<F, C> = bindParallelContinuation(fb, cc)

            c1.start()
            c2.start()

            val resultB = c1.await()
            val resultC = c2.await()

            flatMap(tupled(resultB, resultC)) {
                c.stackLabels = labelHere
                c.resume(it)
                returnedMonad
            }
        }
        COROUTINE_SUSPENDED
    }

    interface BindingInContextContinuation<F, A> : Continuation<A> {
        fun start()

        fun await(): HK<F, A>
    }

    protected fun <B> bindParallelContinuation(fb: HK<F, B>, context: CoroutineContext): BindingInContextContinuation<F, B> =
            object : BindingInContextContinuation<F, B> {
                override fun start() = suspendUnsafeRunAsync(fb).startCoroutine(this)

                var result: HK<F, B>? = null

                val latch: CountDownLatch = CountDownLatch(1)

                override fun await(): HK<F, B> = latch.await().let { result!! }

                override val context: CoroutineContext = context

                override fun resume(value: B) {
                    result = pure(value)
                    latch.countDown()
                }

                override fun resumeWithException(exception: Throwable) {
                    result = raiseError(exception)
                    latch.countDown()
                }
            }

    private fun <B> suspendUnsafeRunAsync(a: HK<F, B>): suspend () -> B {
        return {
            suspendCoroutineOrReturn { cc ->
                MR.unsafeRunAsync(a, { cb ->
                    cb.fold({ cc.resumeWithException(it) }, { cc.resume(it) })
                })
                COROUTINE_SUSPENDED
            }
        }
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
fun <F, B> MonadRun<F, Throwable>.bindingFiber(c: suspend MonadRunContinuation<F, *>.() -> HK<F, B>): Fiber<F, B> {
    val continuation = MonadRunContinuation<F, B>(this)
    c.startCoroutine(continuation, continuation)
    return Fiber(continuation.returnedMonad(), continuation.disposable())
}