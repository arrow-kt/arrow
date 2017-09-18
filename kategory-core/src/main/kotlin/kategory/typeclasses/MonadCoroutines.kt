package kategory

import java.io.Serializable
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.experimental.Continuation
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.EmptyCoroutineContext
import kotlin.coroutines.experimental.RestrictsSuspension
import kotlin.coroutines.experimental.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.experimental.intrinsics.suspendCoroutineOrReturn

@RestrictsSuspension
open class MonadCoroutines<F, A>(M: Monad<F>, override val context: CoroutineContext = EmptyCoroutineContext) :
        Serializable, Continuation<HK<F, A>>, Monad<F> by M {

    override fun resume(value: HK<F, A>) {
        returnedMonad.set(value)
    }

    override fun resumeWithException(exception: Throwable) {
        throw exception
    }

    protected val returnedMonad: AtomicReference<HK<F, A>> = AtomicReference()

    internal fun returnedMonad(): HK<F, A> = returnedMonad.get()

    suspend fun <B> HK<F, B>.bind(): B = bind { this }

    suspend fun <B> bind(m: () -> HK<F, B>): B = suspendCoroutineOrReturn { c ->
        val labelHere = c.stackLabels // save the whole coroutine stack labels
        returnedMonad.set(flatMap(m(), { x: B ->
            c.stackLabels = labelHere
            c.resume(x)
            returnedMonad.get()
        }))
        COROUTINE_SUSPENDED
    }

    infix fun <B> yields(b: B): HK<F, B> = yields { b }

    infix fun <B> yields(b: () -> B): HK<F, B> = pure(b())
}

@RestrictsSuspension
open class StackSafeMonadContinuation<F, A>(M: Monad<F>, override val context: CoroutineContext = EmptyCoroutineContext) :
        Serializable, Continuation<Free<F, A>>, Monad<F> by M {

    override fun resume(value: Free<F, A>) {
        returnedMonad.set(value)
    }

    override fun resumeWithException(exception: Throwable) {
        throw exception
    }

    protected val returnedMonad: AtomicReference<Free<F, A>> = AtomicReference()

    internal fun returnedMonad(): Free<F, A> = returnedMonad.get()

    suspend fun <B> HK<F, B>.bind(): B = bind { Free.liftF(this) }

    suspend fun <B> Free<F, B>.bind(): B = bind { this }

    suspend fun <B> bind(m: () -> Free<F, B>): B = suspendCoroutineOrReturn { c ->
        val labelHere = c.stackLabels // save the whole coroutine stack labels
        returnedMonad.set(m().flatMap { z ->
            c.stackLabels = labelHere
            c.resume(z)
            returnedMonad.get()
        })
        COROUTINE_SUSPENDED
    }

    infix fun <B> yields(b: B): Free<F, B> = yields { b }

    infix fun <B> yields(b: () -> B): Free<F, B> = Free.liftF(pure(b()))
}