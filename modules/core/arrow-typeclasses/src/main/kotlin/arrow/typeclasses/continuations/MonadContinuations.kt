package arrow.typeclasses.continuations

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.ForEval
import arrow.core.fix
import arrow.typeclasses.Awaitable
import arrow.typeclasses.Monad
import arrow.typeclasses.internal.Platform
import arrow.typeclasses.stackLabels
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.EmptyCoroutineContext
import kotlin.coroutines.experimental.startCoroutine
import kotlin.coroutines.experimental.suspendCoroutine

open class MonadBlockingContinuation<F, A>(M: Monad<F>, latch: Awaitable<Kind<F, A>>, override val context: CoroutineContext) :
        Monad<F> by M, Awaitable<Kind<F, A>> by latch, BindingContinuation<F, A> {

    override fun resume(value: Kind<F, A>) {
        resolve(Either.right(value))
    }

    override fun resumeWithException(exception: Throwable) {
        resolve(Either.left(exception))
    }

    protected var returnedMonad: Kind<F, Unit> = M.pure(Unit)

    open fun returnedMonad(): Kind<F, A> =
            awaitBlocking().fold({ throw it }, { it })

    override suspend fun <B> bind(m: () -> Kind<F, B>): B = suspendCoroutine { c ->
        val labelHere = c.stackLabels // save the whole coroutine stack labels
        returnedMonad = flatMap(m(), { x: B ->
            c.stackLabels = labelHere
            c.resume(x)
            returnedMonad
        })
    }

    companion object {
        fun <F, B> binding(M: Monad<F>, context: CoroutineContext = EmptyCoroutineContext, c: suspend (BindingContinuation<F, *>) -> Kind<F, B>): Kind<F, B> {
            val continuation = MonadBlockingContinuation<F, B>(M, Platform.awaitableLatch(), context)
            c.startCoroutine(continuation, continuation)
            return continuation.returnedMonad()
        }
    }
}

class EvalContinuation<A>(M: Monad<ForEval>) :
        BindingContinuation<ForEval, A>, Monad<ForEval> by M {
    override val context: CoroutineContext = EmptyCoroutineContext

    override fun resume(value: Kind<ForEval, A>) {
        returnedMonad = value
    }

    override fun resumeWithException(exception: Throwable) {
        throw exception
    }

    protected lateinit var returnedMonad: Kind<ForEval, A>

    fun returnedMonad(): Kind<ForEval, A> = returnedMonad

    override suspend fun <B> bind(m: () -> Kind<ForEval, B>): B = suspendCoroutine { c ->
        val labelHere = c.stackLabels // save the whole coroutine stack labels
        returnedMonad = m().fix().flatMap { x: B ->
            c.stackLabels = labelHere
            c.resume(x)
            returnedMonad
        }
    }

    companion object {
        fun <A> binding(M: Monad<ForEval>, cc: CoroutineContext, c: suspend BindingContinuation<ForEval, *>.() -> A): Eval<A> =
                Eval.always { }.flatMapIn(cc) {
                    val continuation = EvalContinuation<A>(M)
                    val coro: suspend () -> Eval<A> = { Eval.pure(c(continuation)) }
                    coro.startCoroutine(continuation)
                    continuation.returnedMonad()
                }.fix()
    }
}
