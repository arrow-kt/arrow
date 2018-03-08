package arrow.effects.continuations

import arrow.Kind
import arrow.effects.Effect
import arrow.typeclasses.continuations.BindingCatchContinuation
import arrow.typeclasses.continuations.BindingContinuation
import arrow.typeclasses.stackLabels
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.EmptyCoroutineContext
import kotlin.coroutines.experimental.startCoroutine
import kotlin.coroutines.experimental.suspendCoroutine

class EffectContinuation<F, A>(EF: Effect<F>, val catch: (Throwable) -> Throwable, val bindInContext: CoroutineContext) :
        BindingCatchContinuation<F, Throwable, A>, Effect<F> by EF {

    override val context: CoroutineContext = EmptyCoroutineContext

    override fun resume(value: Kind<F, A>) {
        returnedMonad = value
    }

    override fun resumeWithException(exception: Throwable) {
        returnedMonad = raiseError(catch(exception))
    }

    protected lateinit var returnedMonad: Kind<F, A>

    fun returnedMonad(): Kind<F, A> = returnedMonad

    override suspend fun <B> bind(m: () -> Kind<F, B>): B = suspendCoroutine { c ->
        val labelHere = c.stackLabels // save the whole coroutine stack labels
        returnedMonad = flatMapIn(bindInContext, m(), { x: B ->
            c.stackLabels = labelHere
            c.resume(x)
            returnedMonad
        })
    }

    companion object {
        fun <F, A> bindingIn(EF: Effect<F>, cc: CoroutineContext, c: suspend BindingContinuation<F, *>.() -> A) =
                bindingCatchIn(EF, { it }, cc, c)

        fun <F, A> bindingCatchIn(EF: Effect<F>, catch: (Throwable) -> Throwable, cc: CoroutineContext, c: suspend BindingCatchContinuation<F, Throwable, *>.() -> A) =
                EF.flatMapIn(cc, EF.invoke {}) {
                    val continuation = EffectContinuation<F, A>(EF, catch, cc)
                    val coro: suspend () -> Kind<F, A> = { EF.pure(c(continuation)) }
                    coro.startCoroutine(continuation)
                    EF.mapError(continuation.returnedMonad(), catch)
                }
    }
}