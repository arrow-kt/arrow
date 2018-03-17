package arrow.typeclasses

import arrow.Kind
import arrow.TC
import arrow.core.Either
import arrow.core.Eval
import arrow.typeclass
import arrow.typeclasses.continuations.BindingContinuation
import arrow.typeclasses.continuations.MonadBlockingContinuation
import arrow.typeclasses.internal.Platform
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.EmptyCoroutineContext
import kotlin.coroutines.experimental.startCoroutine

@typeclass
interface Monad<F> : Applicative<F>, TC {

    fun <A, B> flatMap(fa: Kind<F, A>, f: (A) -> Kind<F, B>): Kind<F, B>

    fun <A, B> flatMapIn(cc: CoroutineContext, fa: Kind<F, A>, f: (A) -> Kind<F, B>): Kind<F, B> {
        val continuation = MonadBlockingContinuation<F, B>(this, Platform.awaitableLatch(), cc)
        val coro: suspend () -> Kind<F, B> = { flatMap(fa, f).also { continuation.resolve(Either.right(it)) } }
        coro.startCoroutine(continuation)
        return continuation.returnedMonad()
    }

    override fun <A, B> map(fa: Kind<F, A>, f: (A) -> B): Kind<F, B> = flatMap(fa, { a -> pure(f(a)) })

    override fun <A, B> ap(fa: Kind<F, A>, ff: Kind<F, (A) -> B>): Kind<F, B> = flatMap(ff, { f -> map(fa, f) })

    fun <A> flatten(ffa: Kind<F, Kind<F, A>>): Kind<F, A> = flatMap(ffa, { it })

    fun <A, B> tailRecM(a: A, f: (A) -> Kind<F, Either<A, B>>): Kind<F, B>

    fun <A, B> followedBy(fa: Kind<F, A>, fb: Kind<F, B>): Kind<F, B> = flatMap(fa, { fb })

    fun <A, B> followedByEval(fa: Kind<F, A>, fb: Eval<Kind<F, B>>): Kind<F, B> = flatMap(fa, { fb.value() })

    fun <A, B> forEffect(fa: Kind<F, A>, fb: Kind<F, B>): Kind<F, A> = flatMap(fa, { a -> map(fb, { a }) })

    fun <A, B> forEffectEval(fa: Kind<F, A>, fb: Eval<Kind<F, B>>): Kind<F, A> = flatMap(fa, { a -> map(fb.value(), { a }) })

    /**
     * Entry point for monad bindings which enables for comprehension. The underlying implementation is based on coroutines.
     * A coroutine is initiated and suspended inside [BindingContinuation] yielding to [Monad.flatMap] or [Monad.flatMapIn].
     * Once all the binds are completed the underlying data type is returned from the act of executing the coroutine.
     */
    fun <B> binding(c: suspend BindingContinuation<F, *>.() -> B): Kind<F, B> {
        val continuation = MonadBlockingContinuation<F, B>(this, Platform.awaitableLatch(), EmptyCoroutineContext)
        val coro: suspend () -> Kind<F, B> = { pure(c(continuation)).also { continuation.resolve(Either.right(it)) } }
        coro.startCoroutine(continuation)
        return continuation.returnedMonad()
    }

}
