package katz

import java.io.Serializable
import kotlin.coroutines.experimental.Continuation
import kotlin.coroutines.experimental.EmptyCoroutineContext
import kotlin.coroutines.experimental.RestrictsSuspension
import kotlin.coroutines.experimental.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.experimental.intrinsics.suspendCoroutineOrReturn
import kotlin.coroutines.experimental.startCoroutine

/**
 * The dual of monads, used to extract values from F
 */
interface Comonad<F> : Functor<F>, Typeclass {

    fun <A, B> coflatMap(fa: HK<F, A>, f: (HK<F, A>) -> B): HK<F, B>

    fun <A> extract(fa: HK<F, A>): A

    fun <A> duplicate(fa: HK<F, A>): HK<F, HK<F, A>> =
            coflatMap(fa, { it })
}

@RestrictsSuspension
open class ComonadContinuation<F, A : Any>(val CM: Comonad<F>) : Serializable, Continuation<A> {

    override val context = EmptyCoroutineContext

    override fun resume(value: A) {
        returnedMonad = value
    }

    override fun resumeWithException(exception: Throwable) {
        throw exception
    }

    internal lateinit var returnedMonad: A

    operator suspend fun <B> HK<F, B>.not(): B = bind { this }

    suspend fun <B> HK<F, B>.bind(): B = bind { this }

    suspend fun <B> bind(m: () -> HK<F, B>): B = suspendCoroutineOrReturn { c ->
        val labelHere = c.stackLabels // save the whole coroutine stack labels
        returnedMonad = CM.extract(CM.coflatMap(m(), { x: HK<F, B> ->
            c.stackLabels = labelHere
            c.resume(CM.extract(x))
            returnedMonad
        }))
        COROUTINE_SUSPENDED
    }

    infix fun <B> yields(b: B) = yields { b }

    infix fun <B> yields(b: () -> B) = b()
}

/**
 * Entry point for monad bindings which enables for comprehension. The underlying impl is based on coroutines.
 * A coroutine is initiated and inside `MonadContinuation` suspended yielding to `flatMap` once all the flatMap binds are completed
 * the underlying monad is returned from the act of executing the coroutine
 */
fun <F, B : Any> Comonad<F>.cobinding(c: suspend ComonadContinuation<F, *>.() -> B): B {
    val continuation = ComonadContinuation<F, B>(this)
    c.startCoroutine(continuation, continuation)
    return continuation.returnedMonad
}

inline fun <reified F> comonad(): Comonad<F> =
        instance(InstanceParametrizedType(Comonad::class.java, listOf(F::class.java)))
