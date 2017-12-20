package arrow

import java.io.Serializable
import kotlin.coroutines.experimental.*
import kotlin.coroutines.experimental.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.experimental.intrinsics.suspendCoroutineOrReturn

/**
 * The dual of monads, used to extract values from F
 */
interface Comonad<F> : Functor<F>, Typeclass {

    fun <A, B> coflatMap(fa: HK<F, A>, f: (HK<F, A>) -> B): HK<F, B>

    fun <A> extract(fa: HK<F, A>): A

    fun <A> duplicate(fa: HK<F, A>): HK<F, HK<F, A>> = coflatMap(fa, { it })
}

inline fun <reified F, A, B> HK<F, A>.coflatMap(FT: Comonad<F> = comonad(), noinline f: (HK<F, A>) -> B): HK<F, B> = FT.coflatMap(this, f)

inline fun <reified F, A> HK<F, A>.extract(FT: Comonad<F> = comonad()): A = FT.extract(this)

inline fun <reified F, A> HK<F, A>.duplicate(FT: Comonad<F> = comonad()): HK<F, HK<F, A>> = FT.duplicate(this)

@RestrictsSuspension
open class ComonadContinuation<F, A : Any>(val CM: Comonad<F>, override val context: CoroutineContext = EmptyCoroutineContext) : Serializable, Continuation<A> {

    override fun resume(value: A) {
        returnedMonad = value
    }

    override fun resumeWithException(exception: Throwable) {
        throw exception
    }

    internal lateinit var returnedMonad: A

    suspend fun <B> HK<F, B>.extract(): B = extract { this }

    suspend fun <B> extract(m: () -> HK<F, B>): B = suspendCoroutineOrReturn { c ->
        val labelHere = c.stackLabels // save the whole coroutine stack labels
        returnedMonad = CM.extract(CM.coflatMap(m(), { x: HK<F, B> ->
            c.stackLabels = labelHere
            c.resume(CM.extract(x))
            returnedMonad
        }))
        COROUTINE_SUSPENDED
    }
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

inline fun <reified F> comonad(): Comonad<F> = instance(InstanceParametrizedType(Comonad::class.java, listOf(typeLiteral<F>())))
