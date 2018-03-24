package arrow.typeclasses

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import kotlin.coroutines.experimental.startCoroutine

interface Monad<F> : Applicative<F> {

    fun <A, B> Kind<F, A>.flatMap(f: (A) -> Kind<F, B>): Kind<F, B>

    override fun <A, B> map(fa: Kind<F, A>, f: (A) -> B): Kind<F, B> = fa.flatMap({ a -> pure(f(a)) })

    override fun <A, B> Kind<F, A>.ap(ff: Kind<F, (A) -> B>): Kind<F, B> = ff.flatMap({ f -> map(this, f) })

    fun <A, B> tailRecM(a: A, f: (A) -> Kind<F, Either<A, B>>): Kind<F, B>

    fun <A> Kind<F, Kind<F, A>>.flatten(): Kind<F, A> = this.flatMap({ it })

    fun <A, B> Kind<F, A>.followedBy(fb: Kind<F, B>): Kind<F, B> = this.flatMap({ fb })

    fun <A, B> Kind<F, A>.followedByEval(fb: Eval<Kind<F, B>>): Kind<F, B> = this.flatMap({ fb.value() })

    fun <A, B> Kind<F, A>.forEffect(fb: Kind<F, B>): Kind<F, A> = this.flatMap({ a -> map(fb, { a }) })

    fun <A, B> Kind<F, A>.forEffectEval(fb: Eval<Kind<F, B>>): Kind<F, A> = this.flatMap({ a -> map(fb.value(), { a }) })
}

/**
 * Entry point for monad bindings which enables for comprehension. The underlying implementation is based on coroutines.
 * A coroutine is initiated and suspended inside [MonadErrorContinuation] yielding to [Monad.flatMap]. Once all the flatMap binds are completed
 * the underlying monad is returned from the act of executing the coroutine
 */
fun <F, B> Monad<F>.binding(c: suspend MonadContinuation<F, *>.() -> B): Kind<F, B> {
    val continuation = MonadContinuation<F, B>(this)
    val wrapReturn: suspend MonadContinuation<F, *>.() -> Kind<F, B> = { pure(c()) }
    wrapReturn.startCoroutine(continuation, continuation)
    return continuation.returnedMonad()
}
