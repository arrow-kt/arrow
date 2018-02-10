package arrow.typeclasses

import arrow.Kind
import arrow.TC
import arrow.typeclass
import java.io.Serializable
import kotlin.coroutines.experimental.*
import kotlin.coroutines.experimental.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.experimental.intrinsics.suspendCoroutineOrReturn

/**
 * The dual of monads, used to extract values from F
 */
@typeclass
interface Comonad<F> : Functor<F>, TC {

    fun <A, B> coflatMap(fa: Kind<F, A>, f: (Kind<F, A>) -> B): Kind<F, B>

    fun <A> extract(fa: Kind<F, A>): A

    fun <A> duplicate(fa: Kind<F, A>): Kind<F, Kind<F, A>> = coflatMap(fa, { it })
}

@RestrictsSuspension
open class ComonadContinuation<F, A : Any>(val CM: Comonad<F>, override val context: CoroutineContext = EmptyCoroutineContext) : Serializable, Continuation<A> {

    override fun resume(value: A) {
        returnedMonad = value
    }

    override fun resumeWithException(exception: Throwable) {
        throw exception
    }

    internal lateinit var returnedMonad: A

    suspend fun <B> Kind<F, B>.fix(): B = extract { this }

    suspend fun <B> extract(m: () -> Kind<F, B>): B = suspendCoroutineOrReturn { c ->
        val labelHere = c.stackLabels // save the whole coroutine stack labels
        returnedMonad = CM.extract(CM.coflatMap(m(), { x: Kind<F, B> ->
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