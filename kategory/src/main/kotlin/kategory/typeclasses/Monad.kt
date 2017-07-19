package kategory

import java.io.Serializable
import kotlin.coroutines.experimental.Continuation
import kotlin.coroutines.experimental.EmptyCoroutineContext
import kotlin.coroutines.experimental.RestrictsSuspension
import kotlin.coroutines.experimental.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.experimental.intrinsics.suspendCoroutineOrReturn
import kotlin.coroutines.experimental.startCoroutine

interface Monad<F> : Applicative<F>, Typeclass {

    fun <A, B> flatMap(fa: HK<F, A>, f: (A) -> HK<F, B>): HK<F, B>

    override fun <A, B> ap(fa: HK<F, A>, ff: HK<F, (A) -> B>): HK<F, B> =
            flatMap(ff, { f -> map(fa, f) })

    fun <A> flatten(ffa: HK<F, HK<F, A>>): HK<F, A> =
            flatMap(ffa, { it })

    fun <A, B> tailRecM(a: A, f: (A) -> HK<F, Either<A, B>>): HK<F, B>
}

inline fun <reified F, A, B> HK<F, A>.flatMap(FT: Monad<F> = monad(), noinline f: (A) -> HK<F, B>): HK<F, B> =
        FT.flatMap(this, f)

inline fun <reified F, A, B> HK<F, HK<F, A>>.flatten(FT: Monad<F> = monad()): HK<F, A> =
        FT.flatten(this)

@RestrictsSuspension
open class MonadContinuation<F, A>(val M: Monad<F>) : Serializable, Continuation<HK<F, A>> {

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
        val labelHere = c.stackLabels // save the whole coroutine stack labels
        returnedMonad = M.flatMap(m(), { x ->
            c.stackLabels = labelHere
            c.resume(x)
            returnedMonad
        })
        COROUTINE_SUSPENDED
    }

    infix fun <B> yields(b: B) = yields { b }

    infix fun <B> yields(b: () -> B) = M.pure(b())
}

/**
 * Entry point for monad bindings which enables for comprehension. The underlying impl is based on coroutines.
 * A coroutine is initiated and inside `MonadContinuation` suspended yielding to `flatMap` once all the flatMap binds are completed
 * the underlying monad is returned from the act of executing the coroutine
 */
fun <F, B> Monad<F>.binding(c: suspend MonadContinuation<F, *>.() -> HK<F, B>): HK<F, B> {
    val continuation = MonadContinuation<F, B>(this)
    c.startCoroutine(continuation, continuation)
    return continuation.returnedMonad
}

inline fun <reified F> monad(): Monad<F> =
        instance(InstanceParametrizedType(Monad::class.java, listOf(F::class.java)))
