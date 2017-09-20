package kategory

import kotlin.coroutines.experimental.Continuation
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.EmptyCoroutineContext
import kotlin.coroutines.experimental.RestrictsSuspension
import kotlin.coroutines.experimental.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.experimental.intrinsics.suspendCoroutineOrReturn

@RestrictsSuspension
open class MonadContinuation<F, A>(M: Monad<F>, override val context: CoroutineContext = EmptyCoroutineContext) :
        Continuation<HK<F, A>>, Monad<F> by M {

    override fun resume(value: HK<F, A>) {
        returnedMonad = value
    }

    override fun resumeWithException(exception: Throwable) {
        throw exception
    }

    protected lateinit var returnedMonad: HK<F, A>

    internal fun returnedMonad(): HK<F, A> = returnedMonad

    suspend fun <B> HK<F, B>.bind(): B = bind { this }

    open suspend fun <B> bind(m: () -> HK<F, B>): B = suspendCoroutineOrReturn { c ->
        val labelHere = c.stackLabels // save the whole coroutine stack labels
        returnedMonad = flatMap(m(), { x: B ->
            c.stackLabels = labelHere
            c.resume(x)
            returnedMonad
        })
        COROUTINE_SUSPENDED
    }

    infix fun <B> yields(b: B): HK<F, B> = yields { b }

    infix fun <B> yields(b: () -> B): HK<F, B> = pure(b())
}

@RestrictsSuspension
open class StackSafeMonadContinuation<F, A>(M: Monad<F>, override val context: CoroutineContext = EmptyCoroutineContext) :
        Continuation<Free<F, A>>, Monad<F> by M {

    override fun resume(value: Free<F, A>) {
        returnedMonad = value
    }

    override fun resumeWithException(exception: Throwable) {
        throw exception
    }

    protected lateinit var returnedMonad: Free<F, A>

    internal fun returnedMonad(): Free<F, A> = returnedMonad

    suspend fun <B> HK<F, B>.bind(): B = bind { Free.liftF(this) }

    suspend fun <B> Free<F, B>.bind(): B = bind { this }

    suspend fun <B> bind(m: () -> Free<F, B>): B = suspendCoroutineOrReturn { c ->
        val labelHere = c.stackLabels // save the whole coroutine stack labels
        returnedMonad = m().flatMap { z ->
            c.stackLabels = labelHere
            c.resume(z)
            returnedMonad
        }
        COROUTINE_SUSPENDED
    }

    infix fun <B> yields(b: B): Free<F, B> = yields { b }

    infix fun <B> yields(b: () -> B): Free<F, B> = Free.liftF(pure(b()))
}