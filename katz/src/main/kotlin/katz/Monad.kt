package katz

import kotlin.coroutines.experimental.Continuation
import kotlin.coroutines.experimental.EmptyCoroutineContext
import kotlin.coroutines.experimental.RestrictsSuspension
import kotlin.coroutines.experimental.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.experimental.intrinsics.suspendCoroutineOrReturn
import kotlin.coroutines.experimental.startCoroutine
import java.io.Serializable

interface Monad<F> : Applicative<F> {

    fun <A : Any, B : Any> flatMap(fa: HK<F, A>, f: (A) -> HK<F, B>): HK<F, B>

    override fun <A : Any, B : Any> ap(fa: HK<F, A>, ff: HK<F, (A) -> B>): HK<F, B> =
            flatMap(ff, { f -> map(fa, f) })
}

fun <F, B: Any> Monad<F>.binding(c: suspend MonadContinuation<F, *>.() -> HK<F, B>): HK<F, B> {
    val controller = MonadContinuation<F, B>(this)
    val f: suspend MonadContinuation<F, *>.() -> HK<F, B> = { c() }
    f.startCoroutine(controller, controller)
    return controller.returnedMonad
}

@RestrictsSuspension
class MonadContinuation<F, A : Any>(val M : Monad<F>) : Serializable, Continuation<HK<F, A>> {

    override val context = EmptyCoroutineContext

    override fun resume(value: HK<F, A>) {
        returnedMonad = value
    }

    override fun resumeWithException(exception: Throwable) {
        throw exception
    }

    internal lateinit var returnedMonad: HK<F, A>

    suspend fun <B: Any> bind(m: HK<F, B>): B = suspendCoroutineOrReturn { c ->
        returnedMonad = M.flatMap(m, { x ->
            c.resume(x)
            returnedMonad
        })
        COROUTINE_SUSPENDED
    }

    fun <B : Any> yields(b: B) = M.pure(b)

}

