package katz.typeclasses

import java.io.Serializable
import kotlin.coroutines.experimental.Continuation
import kotlin.coroutines.experimental.EmptyCoroutineContext
import kotlin.coroutines.experimental.RestrictsSuspension
import kotlin.coroutines.experimental.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.experimental.intrinsics.suspendCoroutineOrReturn

interface Monad<F> : Applicative<F> {

    fun <A, B> flatMap(fa: HK<F, A>, f: (A) -> HK<F, B>): HK<F, B>

    override fun <A, B> ap(fa: HK<F, A>, ff: HK<F, (A) -> B>): HK<F, B> =
            flatMap(ff, { f -> map(fa, f) })
}

@RestrictsSuspension
open class MonadContinuation<F, A>(val M : Monad<F>) : Serializable, Continuation<HK<F, A>> {

    override val context = EmptyCoroutineContext

    override fun resume(value: HK<F, A>) {
        returnedMonad = value
    }

    override fun resumeWithException(exception: Throwable) {
        throw exception
    }

    internal lateinit var returnedMonad: HK<F, A>

    operator suspend fun <B> HK<F, B>.not(): B = bind { this }

    suspend fun <B> HK<F, B>.bind(): B = bind { this }

    suspend fun <B> bind(m: () -> HK<F, B>): B = suspendCoroutineOrReturn { c ->
            returnedMonad = M.flatMap(m(), { x ->
                c.resume(x)
                returnedMonad
            })
            COROUTINE_SUSPENDED
        }

    infix fun <B> yields(b: B) = yields { b }

    infix fun <B> yields(b: () -> B) = M.pure(b())

}

