package kategory.effects

import kategory.*
import kategory.effects.data.internal.BindingCancellationException
import kategory.effects.internal.stackLabels
import kotlin.coroutines.experimental.*
import kotlin.coroutines.experimental.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.experimental.intrinsics.suspendCoroutineOrReturn

data class Fiber<out F, out A>(val binding: HK<F, A>, private val dispose: Disposable) : Disposable by dispose

@RestrictsSuspension
open class MonadSuspendContinuation<F, A>(val MR: MonadSuspend<F, Throwable>, AC: AsyncContext<F>, override val context: CoroutineContext = EmptyCoroutineContext) :
        MonadErrorCancellableContinuation<F, A>(MR), MonadSuspend<F, Throwable> by MR, AsyncContext<F> by AC {

    var cancellation: Disposable? = null

    override fun disposable(): Disposable =
            {
                super.disposable()
                cancellation?.apply { invoke() }
            }

    open suspend fun <B, C> bindParallel(cc: CoroutineContext, fa: Fiber<F, B>, fb: Fiber<F, C>): Tuple2<B, C> = suspendCoroutineOrReturn { c ->
        currentCont = c
        val labelHere = c.stackLabels
        returnedMonad = flatMap(pure(Unit)) {
            val c1: BindingInContextContinuation<F, B> = bindParallelContinuation(fa.binding, cc)
            val c2: BindingInContextContinuation<F, C> = bindParallelContinuation(fb.binding, cc)

            bindingECancellable {
                c1.start().bind()
                c2.start().bind()
                val resultB = c1.await().bind()
                val resultC = c2.await().bind()
                c.stackLabels = labelHere
                c.resume(resultB toT resultC)
                returnedMonad
            }.let {
                cancellation = it.b
                it.a
            }
        }
        COROUTINE_SUSPENDED
    }

    open suspend fun <B, C> bindRace(cc: CoroutineContext, fa: Fiber<F, B>, fb: Fiber<F, C>): Either<B, C> = suspendCoroutineOrReturn { c ->
        currentCont = c
        val labelHere = c.stackLabels
        returnedMonad = flatMap(pure(Unit)) {
            val c1: BindingInContextContinuation<F, B> = bindParallelContinuation(raceWith(fa.binding, { fb() }), cc)
            val c2: BindingInContextContinuation<F, C> = bindParallelContinuation(raceWith(fb.binding, { fa() }), cc)

            bindingECancellable {
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
            }.let {
                cancellation = it.b
                it.a
            }
        }
        COROUTINE_SUSPENDED
    }

    protected fun <B> bindParallelContinuation(fb: HK<F, B>, context: CoroutineContext): BindingInContextContinuation<F, B> =
            object : BindingInContextContinuation<F, B> {
                override fun start() = invoke { suspendUnsafeRunAsync(fb).startCoroutine(this) }

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
